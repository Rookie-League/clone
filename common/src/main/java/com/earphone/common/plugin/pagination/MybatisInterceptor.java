package com.earphone.common.plugin.pagination;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author yaojiamin
 * @description
 * @createTime 2015-11-18 上午11:13:27
 */
public abstract class MybatisInterceptor implements Interceptor {
    protected static final transient Logger LOGGER = LoggerFactory.getLogger(PaginationInterceptor.class);
    protected static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    protected static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    protected static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

    @Override
    public Object plugin(Object target) {
        if (getWrapType().isInstance(target)) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }

    protected boolean isNeedPagination(MetaObject metaObject) {
        return metaObject.getOriginalObject() instanceof PageEntity;
    }

    protected abstract Class<?> getWrapType();

    /**
     * @description TODO
     * @createTime 2016-6-12 上午9:48:48
     * @fileName MybatisInterceptor.java
     * @author yaojiamin
     */
    protected MetaObject forObject(Object parameterObject) {
        return MetaObject.forObject(parameterObject, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
    }

    /**
     * @description
     * @createTime 2016-4-19 下午1:26:54
     * @fileName MybatisInterceptor.java
     * @author yaojiamin
     */
    protected MetaObject separateMetaObject(Object object) {
        MetaObject metaObject = forObject(object);
        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环可以分离出最原始的的目标类)
        while (metaObject.hasGetter("h")) {
            Object o = metaObject.getValue("h");
            metaObject = MetaObject.forObject(o, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        // 分离最后一个代理对象的目标类
        while (metaObject.hasGetter("target")) {
            Object o = metaObject.getValue("target");
            metaObject = MetaObject.forObject(o, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        return metaObject;
    }
}
