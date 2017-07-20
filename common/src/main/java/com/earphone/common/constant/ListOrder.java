package com.earphone.common.constant;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/3/10
 * @createTime 8:48
 */
public enum ListOrder {
    ASC("ASC"), DESC("DESC");
    private String value;

    public String getValue() {
        return value;
    }

    ListOrder(String value) {
        this.value = value;
    }

    public static ListOrder parseOrder(String order) {
        try {
            return ListOrder.valueOf(order);
        } catch (Exception ex) {
            return ASC;
        }
    }
}
