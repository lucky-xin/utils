package com.xin.utils.security;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.security.MessageDigest;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: md5加密工具
 * @date 2018-06-21 9:28
 * @Copyright (C)2017 , Luchaoxin
 */
public class DesCrypt {

    private static SecretKey key = null;

    private static Cipher encipher = null;

    private static Cipher decipher = null;

    private static BASE64Decoder base64Decoder = new BASE64Decoder();

    private static BASE64Encoder base64Encoder = new BASE64Encoder();

    private static final String CRYPT_KEY = "rO0ABXNyAB5jb20uc3VuLmNyeXB0by5wcm92aWRlci5ERVNLZXlrNJw12hVomAIAAVsAA2tleXQAAltCeHB1cgACW0Ks8xf4BghU4AIAAHhwAAAACCyDuaGUv2jy";

    public DesCrypt() throws Exception {
        init();
    }

    private void init() throws Exception {
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(this.base64Decoder.decodeBuffer(CRYPT_KEY)));

        this.key = ((SecretKey) objIn.readObject());
        objIn.close();
        objIn = null;
        this.encipher = Cipher.getInstance("DES");
        this.encipher.init(1, this.key);
        this.decipher = Cipher.getInstance("DES");
        this.decipher.init(2, this.key);
    }

    public String encrypt(byte[] data) throws Exception {
        return this.base64Encoder.encode(this.encipher.doFinal(data));
    }

    public byte[] decrypt(String data) throws Exception {
        return this.decipher.doFinal(this.base64Decoder.decodeBuffer(data));
    }

    /**
     * @param text
     * @return
     */
    public static String md5(String text) {
        byte b[];

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            b = md.digest();
        } catch (Exception e) {
            return text;
        }

        int i;
        StringBuffer buf = new StringBuffer("");

        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }

        return buf.toString().toUpperCase();
    }
}