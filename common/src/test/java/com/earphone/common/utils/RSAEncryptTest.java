package com.earphone.common.utils;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static com.earphone.common.constant.Charset.UTF8;
import static com.earphone.common.utils.RSAEncrypt.decrypt;
import static com.earphone.common.utils.RSAEncrypt.encrypt;
import static com.earphone.common.utils.RSAEncrypt.generateKeyPairs;
import static com.earphone.common.utils.RSAEncrypt.loadPrivateKey;
import static com.earphone.common.utils.RSAEncrypt.loadPublicKey;
import static com.earphone.common.utils.RSAEncrypt.sign;
import static com.earphone.common.utils.RSAEncrypt.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/7/20
 * @createTime 9:24
 */
public class RSAEncryptTest {
    @BeforeClass
    public void before() throws Exception {
        generateKeyPairs();
        publicKey = loadPublicKey();
        privateKey = loadPrivateKey();
    }

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private static final String DATA = "ahahahahahahahahahahahahahahahahahaha";

    @Test
    public void testPrivate2Public() throws Exception {
        byte[] encryptedPrivateData = encrypt(privateKey, DATA.getBytes(Charset.forName(UTF8.getValue())));
        String recoveryPrivateData = new String(decrypt(publicKey, encryptedPrivateData), UTF8.getValue());
        assertEquals(recoveryPrivateData, DATA);
    }

    @Test
    public void testPublic2Private() throws Exception {
        byte[] encryptedPublicData = encrypt(publicKey, DATA.getBytes(Charset.forName(UTF8.getValue())));
        String recoveryPublicData = new String(decrypt(privateKey, encryptedPublicData), UTF8.getValue());
        assertEquals(recoveryPublicData, DATA);
    }

    @Test
    public void testSignature() throws Exception {
        byte[] content = DATA.getBytes(Charset.forName(UTF8.getValue()));
        byte[] sign = sign(privateKey, content);
        assertTrue(verify(publicKey, content, sign));
    }
}