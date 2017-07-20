package com.earphone.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static com.earphone.common.utils.ByteExtend.byteArrayToHex;
import static com.earphone.common.utils.ByteExtend.hexStringToBytes;
import static org.testng.Assert.assertEquals;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/7/20
 * @createTime 10:00
 */
@Slf4j
public class ByteExtendTest {
    @Test
    public void test() throws Exception {
        String hex = "0123456789ABCDEF";
        assertEquals(byteArrayToHex(hexStringToBytes(hex)), hex);
    }
}