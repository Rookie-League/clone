package com.earphone.common.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/8/4
 * @createTime 14:09
 */
public class CollectionExtendTest {
    @Test
    public void testIsNotEmpty() throws Exception {
        Assert.assertTrue(CollectionExtend.isNotEmpty(Collections.singletonMap("test", "test")));
        Assert.assertTrue(CollectionExtend.isNotEmpty(Collections.singleton("test")));
        Assert.assertTrue(CollectionExtend.isNotEmpty(Collections.singletonList("test")));
    }

    @Test
    public void testIsEmpty() throws Exception {
        Assert.assertTrue(CollectionExtend.isEmpty((Map<Object, Object>) null));
        Assert.assertTrue(CollectionExtend.isEmpty((Collection<Object>) null));
        Assert.assertTrue(CollectionExtend.isEmpty(Collections.emptyMap()));
        Assert.assertTrue(CollectionExtend.isEmpty(Collections.emptyMap()));
        Assert.assertTrue(CollectionExtend.isEmpty(Collections.emptyList()));
        Assert.assertTrue(CollectionExtend.isEmpty(Collections.emptySet()));
    }
}