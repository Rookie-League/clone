package com.earphone.common.utils;

import com.earphone.common.exception.NonCaptureException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/3/10
 * @createTime 11:01
 */
@Slf4j
public final class JSONExtend {
    private JSONExtend() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toJSON(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NonCaptureException(e);
        }
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NonCaptureException(e);
        }
    }
}
