package com.earphone.common.constant;

/**
 * Created by YaoJiamin on 2016/11/2.
 */

public enum Charset {
    GBK("gbk"), UTF8("utf-8"), ISO88591("iso-8859-1");
    private String value;

    public String getValue() {
        return value;
    }

    Charset(String value) {
        this.value = value;
    }
}
