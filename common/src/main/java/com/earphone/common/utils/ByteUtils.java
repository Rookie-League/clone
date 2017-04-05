package com.earphone.common.utils;

/**
 * Created by YaoJiamin on 2016/11/2.
 */
public class ByteUtils {

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String byteArrayToHex(byte[] data) {
        char[] out = new char[data.length << 1];
        for (int i = 0, j = 0, length = data.length; i < length; i++) {
            out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = DIGITS[(0xF & data[i])];
        }
        return new String(out);
    }
}
