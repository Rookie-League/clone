package com.earphone.utility.validation;

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

    void isTrue(boolean condition, String description) {
        if (condition) return;
        throw new NonCaptureException(description);
    }

    public void equals(T target, String description) {
        T source = getSource();
        isTrue(Objects.isNull(source) ? Objects.isNull(target) : source.equals(target), description);
    }

    public void isEmpty(String description) {
        isTrue(Objects.isNull(getSource()) || invokeEmptyMethod(getSource()), description);
    }

    public void isNotEmpty(String description) {
        isTrue(Objects.nonNull(getSource()) && !invokeEmptyMethod(getSource()), description);
    }

    boolean invokeEmptyMethod(T source) {
        return false;
    }
}
