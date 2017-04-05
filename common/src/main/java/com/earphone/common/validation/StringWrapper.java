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

    public void isNotBlank(String description) {
        isTrue(StringUtils.isNoneBlank(getSource()), description);
    }
}
