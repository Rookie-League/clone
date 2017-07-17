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
    public MapWrapper equals(Map target, String description) {
        return (MapWrapper) super.equals(target, description);
    }

    @Override
    public MapWrapper notEquals(Map target, String description) {
        return (MapWrapper) super.notEquals(target, description);
    }

    @Override
    public MapWrapper isEmpty(String description) {
        return (MapWrapper) super.isEmpty(description);
    }

    @Override
    public MapWrapper isNotEmpty(String description) {
        return (MapWrapper) super.isNotEmpty(description);
    }

    @Override
    boolean invokeEmptyMethod(Map map) {
        return map.isEmpty();
    }
}
