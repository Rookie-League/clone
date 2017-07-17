package com.earphone.common.validation;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/2/6
 * @createTime 16:49
 */
public class BooleanWrapper extends ObjectWrapper<Boolean> {
    BooleanWrapper(Boolean object) {
        super(object);
    }

    @Override
    public BooleanWrapper equals(Boolean target, String description) {
        return (BooleanWrapper) super.equals(target, description);
    }

    @Override
    public BooleanWrapper notEquals(Boolean target, String description) {
        return (BooleanWrapper) super.notEquals(target, description);
    }

    @Override
    public BooleanWrapper isEmpty(String description) {
        return (BooleanWrapper) super.isEmpty(description);
    }

    @Override
    public BooleanWrapper isNotEmpty(String description) {
        return (BooleanWrapper) super.isNotEmpty(description);
    }

    public BooleanWrapper isTrue(String description) {
        return (BooleanWrapper) isTrue(getSource(), description);
    }

    public BooleanWrapper isFalse(String description) {
        return (BooleanWrapper) isTrue(!getSource(), description);
    }
}
