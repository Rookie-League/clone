package com.earphone.common.constant;

/**
 * Created by YaoJiamin on 2016/11/2.
 */

public enum Charset {
    GBK("gbk"), UTF_8("utf-8"), ISO_8859_1("iso-8859-1");
    private String charset;

    public String getCharset() {
        return charset;
    }

    Charset(String charset) {
        this.charset = charset;
    }
}
