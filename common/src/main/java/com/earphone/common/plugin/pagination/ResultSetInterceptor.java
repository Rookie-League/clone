package com.earphone.common.plugin.pagination;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;

import com.earphone.common.constant.Constants;

/**
 * @author yaojiamin
 * @description 结果集拦截器
 * @createTime 2016-6-12 上午10:04:24
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class ResultSetInterceptor extends MybatisInterceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MetaObject metaPage = forObject(getParameterObject(invocation.getTarget()));
        if (isNeedPagination(metaPage) && Objects.isNull(metaPage.getValue(Constants.TOTAL))) {
            // 获取到当前的Statement
            Statement statement = (Statement) invocation.getArgs()[0];
            try (ResultSet resultSet = statement.getResultSet();) {
                Long total = 0L;
                if (resultSet.next()) {
                    total = resultSet.getLong(Constants.TOTAL);
                }
                metaPage.setValue(Constants.TOTAL, total);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            return new ArrayList<>();
        }
        return invocation.proceed();
    }

    /**
     * @description TODO
     * @createTime 2016-6-12 上午10:45:23
     * @fileName ResultSetInterceptor.java
     * @author yaojiamin
     */
    private Object getParameterObject(Object target) {
        return separateMetaObject(target).getValue("parameterHandler.parameterObject");
    }

    /**
     * 只拦截StatementHandler，其他的直接返回目标本身
     */
    @Override
    protected Class<?> getWrapType() {
        return ResultSetHandler.class;
    }

}
