package com.earphone.common.validation;

import java.util.Collection;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/2/6
 * @createTime 16:04
 */
public class CollectionWrapper extends ObjectWrapper<Collection> {
    CollectionWrapper(Collection collection) {
        super(collection);
    }

    @Override
    public CollectionWrapper equals(Collection target, String description) {
        return (CollectionWrapper) super.equals(target, description);
    }

    @Override
    public CollectionWrapper notEquals(Collection target, String description) {
        return (CollectionWrapper) super.notEquals(target, description);
    }

    @Override
    public CollectionWrapper isEmpty(String description) {
        return (CollectionWrapper) super.isEmpty(description);
    }

    @Override
    public CollectionWrapper isNotEmpty(String description) {
        return (CollectionWrapper) super.isNotEmpty(description);
    }

    @Override
    boolean invokeEmptyMethod(Collection collection) {
        return collection.isEmpty();
    }
}
