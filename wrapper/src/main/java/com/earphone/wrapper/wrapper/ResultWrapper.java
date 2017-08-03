package com.earphone.wrapper.wrapper;

import com.earphone.common.constant.ResultType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Value;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/2/8
 * @createTime 13:18
 */
@JsonInclude(Include.NON_NULL)
@Builder
@Value
public class ResultWrapper {
    @JsonIgnore
    private ResultType type;
    private Object result;
    private String error;

    public Integer getCode() {
        return getType().getValue();
    }

    public String getMessage() {
        return getType().getMessage();
    }
}
