package com.earphone.utility.utils;

import com.earphone.common.exception.NonCaptureException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/3/10
 * @createTime 11:01
 */
public class JSONUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);

    private JSONUtils() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toJSON(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new NonCaptureException(e);
        }
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new NonCaptureException(e);
        }
    }
}
