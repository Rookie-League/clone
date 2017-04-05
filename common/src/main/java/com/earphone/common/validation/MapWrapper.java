package com.earphone.common.validation;

import java.util.Map;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/2/6
 * @createTime 16:04
 */
public class MapWrapper extends ObjectWrapper<Map> {
    MapWrapper(Map map) {
        super(map);
    }

    @Override
    boolean invokeEmptyMethod(Map map) {
        return map.isEmpty();
    }
}
