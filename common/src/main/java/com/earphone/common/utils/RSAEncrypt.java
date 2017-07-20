package com.earphone.common.utils;

import com.earphone.common.exception.CapturedException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static com.earphone.common.utils.PathExtend.classpath;

@Slf4j
public final class RSAEncrypt {
    public static final String DEFAULT_PRIVATE_KEY_PATH = classpath().concat("/privateKey.keystore");
    public static final String DEFAULT_PUBLIC_KEY_PATH = classpath().concat("/publicKey.keystore");

    private RSAEncrypt() {
    }

    /**
     * 随机生成密钥对
     */
    public static void generateKeyPairs() throws Exception {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen;
        keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        // keyPairGen.initialize(1024,new SecureRandom());
        keyPairGen.initialize(1024);
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        writeKeyToFile(DEFAULT_PRIVATE_KEY_PATH, keyPair.getPrivate());
        // 得到公钥
        writeKeyToFile(DEFAULT_PUBLIC_KEY_PATH, keyPair.getPublic());
    }

    private static void writeKeyToFile(String filePath, Key key) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 得到密钥字符串,将密钥对写入到文件
            writer.write(Base64Extend.encodeBytes(key.getEncoded()));
            // writer.write(byteArrayToHexString(key.getEncoded()));
            writer.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static String loadKeyAsString(String path) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            for (String readLine; (readLine = br.readLine()) != null; ) {
                sb.append(readLine);
            }
            return sb.toString();
        }
    }

    /**
     * 从默认文件中加载公钥
     */
    public static RSAPublicKey loadPublicKey() throws Exception {
        return loadPublicKey(DEFAULT_PUBLIC_KEY_PATH);
    }

    /**
     * 从指定文件路径中加载公钥
     */
    public static RSAPublicKey loadPublicKey(String path) throws Exception {
        String keyString = loadKeyAsString(DEFAULT_PUBLIC_KEY_PATH);
        return loadPublicKeyFromString(keyString);
    }

    /**
     * 从字符串中加载公钥
     */
    private static RSAPublicKey loadPublicKeyFromString(String publicKeyString) throws Exception {
        byte[] buffer = Base64Extend.decodeToBytes(publicKeyString.getBytes());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 从默认文件中加载私钥
     */
    public static RSAPrivateKey loadPrivateKey() throws Exception {
        return loadPrivateKey(DEFAULT_PRIVATE_KEY_PATH);
    }

    /**
     * 从指定文件路径中加载私钥
     */
    public static RSAPrivateKey loadPrivateKey(String path) throws Exception {
        String keyString = loadKeyAsString(path);
        return loadPrivateKeyFromString(keyString);
    }

    private static RSAPrivateKey loadPrivateKeyFromString(String privateKeyStr) throws Exception {
        byte[] buffer = Base64Extend.decodeToBytes(privateKeyStr.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥加密过程
     *
     * @param publicKey     公钥
     * @param plainTextData 明文数据
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        if (publicKey == null) {
            throw new CapturedException("Public key is null");
        }
        // 使用默认RSA
        Cipher cipher = Cipher.getInstance("RSA");
        // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainTextData);
    }

    /**
     * 私钥加密过程
     *
     * @param privateKey    私钥
     * @param plainTextData 明文数据
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception {
        if (privateKey == null) {
            throw new CapturedException("Private key is null");
        }
        // 使用默认RSA
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(plainTextData);
    }

    /**
     * 私钥解密过程
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文数据
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
        if (privateKey == null) {
            throw new CapturedException("Private key is null");
        }
        // 使用默认RSA
        Cipher cipher = Cipher.getInstance("RSA");
        // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(cipherData);
    }

    /**
     * 公钥解密过程
     *
     * @param publicKey  公钥
     * @param cipherData 密文数据
     * @return 明文数据
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
        if (publicKey == null) {
            throw new CapturedException("Public key is null");
        }
        // 使用默认RSA
        Cipher cipher = Cipher.getInstance("RSA");
        // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(cipherData);
    }

    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHMS = "SHA256withRSA";

    /**
     * RSA签名
     *
     * @param privateKey 私钥
     * @param content    原始数据
     * @return 签名值
     */
    public static byte[] sign(PrivateKey privateKey, byte[] content) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(privateKey);
        signature.update(content);
        return signature.sign();
    }

    /**
     * RSA验签名检查
     *
     * @param pubKey  公钥
     * @param content 原始数据
     * @param sign    签名值
     * @return 布尔值
     */
    public static boolean verify(PublicKey pubKey, byte[] content, byte[] sign) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initVerify(pubKey);
        signature.update(content);
        return signature.verify(sign);
    }
}
