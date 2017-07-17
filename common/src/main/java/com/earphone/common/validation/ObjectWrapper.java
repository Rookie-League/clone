package com.earphone.common.validation;

import com.earphone.common.exception.NonCaptureException;

import java.util.Objects;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/2/6
 * @createTime 16:04
 */
public class ObjectWrapper<T> {
    private T object;

    T getSource() {
        return object;
    }

    ObjectWrapper(T object) {
        this.object = object;
    }

    ObjectWrapper<T> isTrue(boolean condition, String description) {
        if (condition) {
            return this;
        }
        throw new NonCaptureException(description);
    }

    public ObjectWrapper<T> equals(T target, String description) {
        T source = getSource();
        return isTrue(Objects.isNull(source) ? Objects.isNull(target) : source.equals(target), description);
    }

    public ObjectWrapper<T> notEquals(T target, String description) {
        T source = getSource();
        return isTrue(Objects.isNull(source) ? Objects.nonNull(target) : source.equals(target), description);
    }

    public ObjectWrapper<T> isEmpty(String description) {
        T source = getSource();
        return isTrue(Objects.isNull(source) || invokeEmptyMethod(source), description);
    }

    public ObjectWrapper<T> isNotEmpty(String description) {
        T source = getSource();
        return isTrue(Objects.nonNull(source) && !invokeEmptyMethod(source), description);
    }

    boolean invokeEmptyMethod(T source) {
        return false;
    }
}
