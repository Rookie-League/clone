package com.earphone.common.constant;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/3/10
 * @createTime 8:48
 */
public enum ListOrder {
    ASC("asc"), DESC("desc");
    public String order;

    ListOrder(String order) {
        this.order = order;
    }

    public static ListOrder parseOrder(String order) {
        for (ListOrder listOrder : ListOrder.values()) {
            if (listOrder.order.equals(order.toLowerCase().trim())) {
                return listOrder;
            }
        }
        return ASC;
    }
}
