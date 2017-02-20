package com.earphone.utility.validation;

import java.util.Collection;
import java.util.Map;

/**
 * @author yaojiamin
 * @description
 * @createTime 2016-4-6 下午2:39:57
 */
public class Assert {

    public static ObjectWrapper<Object> wrapObject(Object object) {
        return new ObjectWrapper<>(object);
    }

    public static BooleanWrapper wrapBoolean(Boolean object) {
        return new BooleanWrapper(object);
    }

    public static <T extends CharSequence> StringWrapper wrapString(T object) {
        return new StringWrapper(object);
    }

    public static <T extends Map> MapWrapper wrapCollection(T object) {
        return new MapWrapper(object);
    }

    public static <T extends Collection> CollectionWrapper wrapCollection(T object) {
        return new CollectionWrapper(object);
    }

}
