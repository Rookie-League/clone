package com.earphone.common.plugin.pagination;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.earphone.common.constant.Constants;
import com.earphone.utility.validation.Assert;

/**
 * 采用分页拦截器执行物理分页查询时，原生的Executor创建cacheKey时未能包含分页参数page，为了解决这个问题，创建了本拦截器，
 * 本拦截器会拦截CachingExecutor的query方法，在创建cacheKey时将分页参数page包含其中。 老规矩，签名里要拦截的类型只能是接口。
 *
 * @author 湖畔微风
 */
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class CacheInterceptor extends MybatisInterceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            return this.query(castExecutor(invocation.getTarget()), new CacheHandlerBean(invocation.getArgs()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return invocation.proceed();
    }

    /**
     * @description TODO
     * @createTime 2016-5-31 下午4:53:37
     * @fileName CacheInterceptor.java
     * @author yaojiamin
     */
    private Executor castExecutor(Object object) {
        return (Executor) separateMetaObject(object).getOriginalObject();
    }

    /**
     * @description TODO
     * @createTime 2016-5-31 下午4:53:32
     * @fileName CacheInterceptor.java
     * @author yaojiamin
     */
    private <E> List<E> query(Executor executor, CacheHandlerBean cacheHandlerBean) throws Exception {
        BoundSql boundSql = cacheHandlerBean.getMappedStatement().getBoundSql(cacheHandlerBean.getParameterObject());
        CacheKey cacheKey = executor.createCacheKey(cacheHandlerBean.getMappedStatement(), cacheHandlerBean.getParameterObject(), cacheHandlerBean.getRowBounds(), boundSql);
        MetaObject metaPage = forObject(cacheHandlerBean.getParameterObject());
        // 当需要分页查询时，将page参数里的当前页和每页数加到cachekey里
        if (isNeedPagination(metaPage)) {
            executeQuery(cacheHandlerBean, createPageBoundSql(cacheHandlerBean, boundSql), cloneCacheKey(cacheKey), executor);
            Assert.wrapObject(metaPage.getValue(Constants.TOTAL)).isNotEmpty("Fail to count result!!!");
            cacheKey.update(metaPage.getValue(Constants.PAGE_NO));
            cacheKey.update(metaPage.getValue(Constants.PAGE_SIZE));
        }
        return executeQuery(cacheHandlerBean, boundSql, cacheKey, executor);
    }

    /**
     * @description TODO
     * @createTime 2016-4-27 下午12:33:11
     * @fileName CacheInterceptor.java
     * @author yaojiamin
     */
    private <E> List<E> executeQuery(CacheHandlerBean cacheHandlerBean, BoundSql boundSql, CacheKey cacheKey, Executor executor) throws SQLException {
        return executor.query(cacheHandlerBean.getMappedStatement(), cacheHandlerBean.getParameterObject(), cacheHandlerBean.getRowBounds(), cacheHandlerBean.getResultHandler(), cacheKey, boundSql);
    }

    /**
     * @description TODO
     * @createTime 2016-4-19 上午11:20:47
     * @fileName CacheInterceptor.java
     * @author yaojiamin
     */
    private BoundSql createPageBoundSql(CacheHandlerBean cacheHandlerBean, BoundSql boundSql) {
        return new BoundSql(cacheHandlerBean.getMappedStatement().getConfiguration(), createCountSql(boundSql), boundSql.getParameterMappings(), cacheHandlerBean.getParameterObject());
    }

    /**
     * @description TODO
     * @createTime 2016-4-19 上午11:20:11
     * @fileName CacheInterceptor.java
     * @author yaojiamin
     */
    private String createCountSql(BoundSql boundSql) {
        return "select ifnull(count(0),0) as total from (" + boundSql.getSql() + ") t";
    }

    /**
     * @description TODO
     * @createTime 2016-4-19 上午11:12:22
     * @fileName CacheInterceptor.java
     * @author yaojiamin
     */
    private CacheKey cloneCacheKey(CacheKey cacheKey) {
        try {
            return cacheKey.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.error("Failed to clone CacheKey!!!", e);
        }
        return cacheKey;
    }

    /**
     * 只拦截CachingExecutor，其他的直接返回目标本身
     */
    @Override
    protected Class<?> getWrapType() {
        return CachingExecutor.class;
    }

    private class CacheHandlerBean {
        private MappedStatement mappedStatement;
        private RowBounds rowBounds;
        private ResultHandler<?> resultHandler;
        private Object parameterObject;

        /**
         * @param args
         */
        @SuppressWarnings("unchecked")
        CacheHandlerBean(Object[] args) {
            mappedStatement = (MappedStatement) args[0];
            parameterObject = args[1];
            rowBounds = (RowBounds) args[2];
            resultHandler = (ResultHandler<?>) args[3];
        }

        /**
         * @return the mappedStatement
         */
        MappedStatement getMappedStatement() {
            return mappedStatement;
        }

        /**
         * @return the rowBounds
         */
        RowBounds getRowBounds() {
            return rowBounds;
        }

        /**
         * @return the resultHandler
         */
        ResultHandler<?> getResultHandler() {
            return resultHandler;
        }

        /**
         * @return the parameterObject
         */
        Object getParameterObject() {
            return parameterObject;
        }
    }
}
