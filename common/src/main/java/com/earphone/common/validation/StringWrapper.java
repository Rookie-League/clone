package com.earphone.common.validation;

import org.apache.commons.lang3.StringUtils;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/2/6
 * @createTime 16:04
 */
public class StringWrapper extends ObjectWrapper<CharSequence> {
    StringWrapper(CharSequence text) {
        super(text);
    }

    @Override
    boolean invokeEmptyMethod(CharSequence source) {
        return source.length() == 0;
    }

    public StringWrapper isNotBlank(String description) {
        return (StringWrapper) isTrue(StringUtils.isNoneBlank(getSource()), description);
    }

    @Override
    public StringWrapper equals(CharSequence target, String description) {
        return (StringWrapper) super.equals(target, description);
    }

    @Override
    public StringWrapper notEquals(CharSequence target, String description) {
        return (StringWrapper) super.notEquals(target, description);
    }

    @Override
    public StringWrapper isEmpty(String description) {
        return (StringWrapper) super.isEmpty(description);
    }

    @Override
    public StringWrapper isNotEmpty(String description) {
        return (StringWrapper) super.isNotEmpty(description);
    }
}
