package com.earphone.common.response;

import com.earphone.common.constant.ResultType;
import net.sf.json.JSONObject;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2016/12/13
 * @createTime 16:46
 */
public class WebResponse {
    private WebResponse(ResultType code, String message) {
        setCode(code);
        setMessage(message);
    }

    private ResultType code;
    private String message;

    public ResultType getCode() {
        return code;
    }

    private void setCode(ResultType code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return JSONObject.fromObject(this).toString();
    }

    public static WebResponse of(ResultType code, String message) {
        return new WebResponse(code, message);
    }
}
