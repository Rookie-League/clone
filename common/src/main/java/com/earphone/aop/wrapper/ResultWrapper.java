package com.earphone.aop.wrapper;

import com.earphone.common.constant.ResultType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/2/8
 * @createTime 13:18
 */
@JsonInclude(Include.NON_NULL)
public class ResultWrapper {
    private ResultWrapper(WrapperBuilder builder) {
        setType(builder.type);
        setResult(builder.result);
        setError(builder.error);
    }

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

    public ResultType getType() {
        return type;
    }

    private void setType(ResultType type) {
        this.type = type;
    }

    public Object getResult() {
        return result;
    }

    private void setResult(Object result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    private void setError(String error) {
        this.error = error;
    }

    public static class WrapperBuilder {
        public WrapperBuilder() {
            setType(ResultType.SUCCESS);
        }

        private ResultType type;
        private Object result;
        private String error;

        public WrapperBuilder setType(ResultType type) {
            this.type = type;
            return this;
        }

        public WrapperBuilder setResult(Object result) {
            this.result = result;
            return this;
        }

        public WrapperBuilder setError(String error) {
            this.error = error;
            return this;
        }

        public ResultWrapper builder() {
            return new ResultWrapper(this);
        }
    }
}
