package com.earphone.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/8/4
 * @createTime 13:00
 */
@Slf4j
public class WebExtendTest {
    @Test
    public void testGet() throws Exception {
        Assert.assertTrue(isNotBlank(WebExtend.get("https://baidu.com")));
    }

    @Test
    public void testPost() throws Exception {
        Assert.assertTrue(isNotBlank(WebExtend.post("https://baidu.com", Collections.singletonMap("test", "test"))));
    }
}