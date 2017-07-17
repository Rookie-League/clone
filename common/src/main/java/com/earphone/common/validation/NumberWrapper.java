package com.earphone.common.validation;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/6/23
 * @createTime 14:01
 */
public class NumberWrapper extends ObjectWrapper<Number> {
    NumberWrapper(Number object) {
        super(object);
    }

    @Override
    public NumberWrapper equals(Number target, String description) {
        return (NumberWrapper) super.equals(target, description);
    }

    @Override
    public NumberWrapper notEquals(Number target, String description) {
        return (NumberWrapper) super.notEquals(target, description);
    }

    @Override
    public NumberWrapper isEmpty(String description) {
        return (NumberWrapper) super.isEmpty(description);
    }

    @Override
    public NumberWrapper isNotEmpty(String description) {
        return (NumberWrapper) super.isNotEmpty(description);
    }

    public NumberWrapper greater(Number target, String description) {
        Number source = getSource();
        return (NumberWrapper) isTrue(source.doubleValue() > target.doubleValue(), description);
    }

    public NumberWrapper notGreater(Number target, String description) {
        Number source = getSource();
        return (NumberWrapper) isTrue(source.doubleValue() <= target.doubleValue(), description);
    }

    public NumberWrapper lesser(Number target, String description) {
        Number source = getSource();
        return (NumberWrapper) isTrue(source.doubleValue() < target.doubleValue(), description);
    }

    public NumberWrapper notLesser(Number target, String description) {
        Number source = getSource();
        return (NumberWrapper) isTrue(source.doubleValue() >= target.doubleValue(), description);
    }
}