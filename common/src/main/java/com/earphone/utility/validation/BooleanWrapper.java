package com.earphone.utility.validation;

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

    public void isTrue(String description) {
        isTrue(getSource(), description);
    }

    public void isFalse(String description) {
        isTrue(!getSource(), description);
    }
}
