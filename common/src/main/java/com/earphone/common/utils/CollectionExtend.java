package com.earphone.common.utils;

import java.util.Collection;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/8/4
 * @createTime 10:56
 */
public final class CollectionExtend {
    private static final CollectionExtend COLLECTION_EXTEND = new CollectionExtend();

    private CollectionExtend() {
    }

    public static <Key, Value> boolean isNotEmpty(Map<Key, Value> map) {
        return !isEmpty(map);
    }

    public static <Key, Value> boolean isEmpty(Map<Key, Value> map) {
        return isNull(map) || map.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return isNull(collection) || collection.isEmpty();
    }
}
