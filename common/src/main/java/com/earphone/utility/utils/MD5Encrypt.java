package com.earphone.utility.utils;

import com.earphone.common.constant.Charset;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Created by YaoJiamin on 2016/10/28.
 */

public final class MD5Encrypt {
    private MD5Encrypt() {
    }

    public static String md5(String text) {
        return md5(text, Charset.UTF_8);
    }

    public static String md5(String text, Charset charset) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest msgDigest = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            msgDigest.update(text.getBytes(charset.getCharset()));
            // 获得密文,把密文转换成十六进制的字符串形式
            return ByteUtils.byteArrayToHex(msgDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No Such Algorithm.");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported Encoding.");
        }
    }

    public static Boolean equals(String original, String ciphertext) {
        return equals(original, ciphertext, Charset.UTF_8);
    }

    public static Boolean equals(String original, String ciphertext, Charset charset) {
        return Objects.nonNull(original) && Objects.nonNull(ciphertext) && ciphertext.equals(md5(original, charset)) ? Boolean.TRUE : Boolean.FALSE;
    }
}