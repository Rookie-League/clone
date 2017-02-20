package com.earphone.common.constant;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2016/12/13
 * @createTime 16:35
 */
public enum ResultType {
    SUCCESS("success", 0), FAILURE("failure", 1);
    private String message;
    private int value;

    public String getMessage() {
        return message;
    }

    public int getValue() {
        return value;
    }

    ResultType(String message, int value) {
        this.message = message;
        this.value = value;
    }
}
