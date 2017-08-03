package com.earphone.wrapper;

import com.earphone.common.utils.StringExtend;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.earphone.common.utils.JSONExtend.asPrettyJSON;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/8/3
 * @createTime 18:40
 */
@Builder
@Value
@Slf4j
class WrapRecord {
    private boolean serialize;
    private String signature;
    private String point;
    private Object[] arguments;
    private Object result;

    void record() {
        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        builder.append("\nInvoke:{},");
        list.add(signature);
        if (StringExtend.isNotBlank(point)) {
            builder.append("\nLog={},");
            list.add(point);
        }
        if (serialize) {
            builder.append("\nArgument={},");
            list.add(asPrettyJSON(arguments));
        }
        builder.append("\nResult={}");
        list.add(basicType(this.result) ? this.result : asPrettyJSON(this.result));
        log.info(builder.toString(), list.toArray());
    }

    private boolean basicType(Object result) {
        return result instanceof CharSequence || result instanceof Number || result instanceof Character || result instanceof Boolean;
    }
}
