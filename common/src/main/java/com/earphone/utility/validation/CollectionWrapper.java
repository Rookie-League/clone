package com.earphone.utility.validation;

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
    boolean invokeEmptyMethod(Collection collection) {
        return collection.isEmpty();
    }
}
