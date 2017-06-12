package com.earphone.common.utils;

import com.earphone.common.constant.Charset;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Created by YaoJiamin on 2016/10/28.
 */

public final class IrreversibleEncrypt {
    private IrreversibleEncrypt() {
    }

    private enum EncryptType {
        MD5("MD5"), SHA1("SHA-1");

        private String type;

        EncryptType(String type) {
            this.type = type;
        }
    }

    public static String sha1(String text) {
        return encrypt(text, Charset.UTF_8, EncryptType.SHA1);
    }

    public static String md5(String text) {
        return encrypt(text, Charset.UTF_8, EncryptType.MD5);
    }

    public static String encrypt(String text, Charset charset, EncryptType type) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest msgDigest = MessageDigest.getInstance(type.type);
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

    public static Boolean equalsSHA1(String original, String ciphertext) {
        return equals(original, ciphertext, Charset.UTF_8, EncryptType.SHA1);
    }

    public static Boolean equalsMD5(String original, String ciphertext) {
        return equals(original, ciphertext, Charset.UTF_8, EncryptType.MD5);
    }

    public static Boolean equals(String original, String ciphertext, Charset charset, EncryptType type) {
        return Objects.nonNull(original) && Objects.nonNull(ciphertext) && ciphertext.equals(encrypt(original, charset, type)) ? Boolean.TRUE : Boolean.FALSE;
    }
}