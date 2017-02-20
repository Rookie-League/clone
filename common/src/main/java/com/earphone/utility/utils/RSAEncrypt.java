package com.earphone.utility.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncrypt {

    private static String filePath = "C:/Users/littlearphone/Desktop/rsa/";
    private static String privateKeyPath = filePath + "/privateKey.keystore";
    private static String publicKeyPath = filePath + "/publicKey.keystore";

//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        genKeyPair("C:/Users/littlearphone/Desktop/rsa/");
//    }
//
//    /**
//     * 随机生成密钥对
//     */
//    public static void genKeyPair(String filePath) throws NoSuchAlgorithmException {
//        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
//        KeyPairGenerator keyPairGen;
//        try {
//            keyPairGen = KeyPairGenerator.getInstance("RSA");
//        } catch (NoSuchAlgorithmException e) {
//            throw e;
//        }
//        // 初始化密钥对生成器，密钥大小为96-1024位
//        // keyPairGen.initialize(1024,new SecureRandom());
//        keyPairGen.initialize(1024);
//        // 生成一个密钥对，保存在keyPair中
//        KeyPair keyPair = keyPairGen.generateKeyPair();
//        // 得到私钥
//        writeKeyToFile(filePath + "/privateKey.keystore", keyPair.getPrivate());
//        // 得到公钥
//        writeKeyToFile(filePath + "/publicKey.keystore", keyPair.getPublic());
//    }
//
//    private static void writeKeyToFile(String filePath, Key publicKey) {
//        try (BufferedWriter pubbw = new BufferedWriter(new FileWriter(filePath))) {
//            // 得到密钥字符串,将密钥对写入到文件
//            pubbw.write(Base64Utils.encodeBytes(publicKey.getEncoded()));
//            // pubbw.write(byteArrayToHexString(publicKey.getEncoded()));
//            pubbw.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private static String loadKeyFromFile(String path) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            for (String readLine; (readLine = br.readLine()) != null; sb.append(readLine)) ;
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("密钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("密钥输入流为空");
        }
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKeyOfFile() throws Exception {
        String keyString = loadKeyFromFile(publicKeyPath);
        return loadPublicKeyOfString(keyString);
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyString 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKeyOfString(String publicKeyString) throws Exception {
        try {
            byte[] buffer = Base64Utils.decodeToBytes(publicKeyString.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @return 是否成功
     */
    public static RSAPrivateKey loadPrivateKeyOfFile() throws Exception {
        String keyString = loadKeyFromFile(privateKeyPath);
        return loadPrivateKeyOfString(keyString);
    }

    public static RSAPrivateKey loadPrivateKeyOfString(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64Utils.decodeToBytes(privateKeyStr.getBytes());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
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
            throw new Exception("加密公钥为空, 请设置");
        }
        try {
            // 使用默认RSA
            Cipher cipher = Cipher.getInstance("RSA");
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainTextData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            throw new Exception("无此填充机制");
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
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
            throw new Exception("加密私钥为空, 请设置");
        }
        try {
            // 使用默认RSA
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(plainTextData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            throw new Exception("无此填充机制");
        } catch (InvalidKeyException e) {
            throw new Exception("加密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 私钥解密过程
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }
        try {
            // 使用默认RSA
            Cipher cipher = Cipher.getInstance("RSA");
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            throw new Exception("无此填充机制");
        } catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    /**
     * 公钥解密过程
     *
     * @param publicKey  公钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }
        try {
            // 使用默认RSA
            Cipher cipher = Cipher.getInstance("RSA");
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            throw new Exception("无此填充机制");
        } catch (InvalidKeyException e) {
            throw new Exception("解密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    /**
     * 字节数据转十六进制字符串
     *
     * @param bytes 输入数据
     * @return 十六进制内容
     */
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
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
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(privateKey);
            signature.update(content);
            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (SignatureException e) {
            throw new Exception("签名错误");
        } catch (InvalidKeyException e) {
            throw new Exception("私钥非法");
        }
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
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content);
            return signature.verify(sign);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (SignatureException e) {
            throw new Exception("签名错误");
        } catch (InvalidKeyException e) {
            throw new Exception("私钥非法");
        }
    }
}
