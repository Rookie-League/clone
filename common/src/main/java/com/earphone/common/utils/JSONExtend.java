package com.earphone.common.utils;

import com.earphone.common.exception.NonCaptureException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/3/10
 * @createTime 11:01
 */
public final class JSONExtend {
    private JSONExtend() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String asJSON(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new NonCaptureException(e);
        }
    }

    public static String asPrettyJSON(Object object) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            throw new NonCaptureException(e);
        }
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new NonCaptureException(e);
        }
    }
}
