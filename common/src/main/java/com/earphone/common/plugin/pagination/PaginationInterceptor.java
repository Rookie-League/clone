package com.earphone.common.plugin.pagination;

import java.sql.Connection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.RowBounds;

import com.earphone.common.constant.Constants;
import com.earphone.common.plugin.pagination.PageEntity.ListOrder;

/**
 * @author yaojiamin
 * @description 分页拦截器
 * @createTime 2015-11-18 上午10:52:12
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PaginationInterceptor extends MybatisInterceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MetaObject metaStatement = separateMetaObject(invocation.getTarget());
        // 查询标签对应的实体参数，传递给mapper的对象
        Object parameterObject = metaStatement.getValue("delegate.boundSql.parameterObject");
        MetaObject metaPage = forObject(parameterObject);
        if (isNeedPagination(metaPage) && metaPage.getValue(Constants.TOTAL) != null) {
            try {
                // 获取查询绑定的SQL
                String sql = metaStatement.getValue("delegate.boundSql.sql").toString();
                metaStatement.setValue("delegate.boundSql.sql", packSql(sql, parameterObject));
                // 采用物理分页后，就不需要mybatis的内存分页了，所以重置下面的两个参数
                metaStatement.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
                metaStatement.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
            } catch (Exception e) {
                LOGGER.error("Pagination Exception", e);
            }
        }
        return invocation.proceed();
    }

    /**
     * @description 进行sql包装
     * @createTime 2015-11-18 上午10:52:41
     * @fileName PaginationInterceptor.java
     * @author yaojiamin
     */
    private String packSql(String sql, Object parameterObject) {
        if (Objects.isNull(parameterObject)) return sql;
        PageEntity<?> page = (PageEntity<?>) parameterObject;
        StringBuilder pageSql = new StringBuilder(sql);
        pageSql.append(mysqlOrderBy(page.getOrderMap()));
        pageSql.append(mysqlPagination(page.getStart(), page.getPageSize()));
        return pageSql.toString();
    }

    /**
     * @description 获取mysql分页子句
     * @createTime 2015-11-18 上午10:48:10
     * @fileName PaginationInterceptor.java
     * @author yaojiamin
     */
    private String mysqlPagination(int start, int pageSize) {
        return " limit " + start + "," + pageSize;
    }

    /**
     * @description 获取mysql排序子句
     * @createTime 2015-11-18 上午10:52:29
     * @fileName PaginationInterceptor.java
     * @author yaojiamin
     */
    private String mysqlOrderBy(Map<String, ListOrder> orderMap) {
        StringBuilder orderByClause = new StringBuilder(" ");
        if (Objects.nonNull(orderMap) && !orderMap.isEmpty()) {
            orderByClause.append(" order by ");
            for (Entry<String, ListOrder> entry : orderMap.entrySet()) {
                orderByClause.append(entry.getKey()).append(" ").append(entry.getValue().order).append(",");
            }
        }
        return orderByClause.length() > 0 ? orderByClause.substring(0, orderByClause.length() - 1) : orderByClause.toString();
    }

    /**
     * 只拦截StatementHandler，其他的直接返回目标本身
     */
    @Override
    protected Class<?> getWrapType() {
        return StatementHandler.class;
    }

}
