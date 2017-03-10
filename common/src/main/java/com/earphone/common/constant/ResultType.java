package com.earphone.common.constant;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2016/12/13
 * @createTime 16:35
 */
public enum ResultType {
    FAILURE("FAILURE", -1),
    SUCCESS("SUCCESS", 0);

    private String message;
    private Integer value;

    public String getMessage() {
        return message;
    }

    public Integer getValue() {
        return value;
    }

    ResultType(String message, Integer value) {
        this.message = message;
        this.value = value;
    }
}
