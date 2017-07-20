package com.earphone.common.utils;

/**
 * Created by YaoJiamin on 2016/11/2.
 */
public final class ByteExtend {
    private ByteExtend() {
    }

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String byteArrayToHex(byte[] data) {
        char[] out = new char[data.length << 1];
        for (int i = 0, j = 0, length = data.length; i < length; i++) {
            out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = DIGITS[(0xF & data[i])];
        }
        return new String(out);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.trim().equals("")) {
            throw new IllegalArgumentException("无效的密文数据");
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            bytes[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return bytes;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
