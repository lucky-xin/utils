package com.xin.utils.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: AES加密解密
 * @date 2018-08-13 19:07
 * @Copyright (C)2018 , Luchaoxin
 */
public class AesEncryptUtil {

    /**
     * 加密方法
     *
     * @param data 要加密的数据
     * @param key  加密key
     * @param iv   加密iv
     * @return 加密的结果
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        //"算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();

        byte[] dataBytes = data.getBytes();
        int plaintextLength = dataBytes.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }

        byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(plaintext);

        return new Base64().encodeToString(encrypted);

    }

    /**
     * 解密方法
     *
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv   解密iv
     * @return 解密的结果
     * @throws Exception
     */
    public static String desEncrypt(String data, String key, String iv) throws Exception {

        byte[] encrypted1 = new Base64().decode(data);

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original);

        return originalString;
    }

    public static String getJsBase64String(String secretKey) {
        return Base64.encodeBase64String(secretKey.getBytes());
    }

    //使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
    private static String KEY = "ZGIyMTM5NTYxYzlmZTA2OA==";

    private static String IV = "ZGIyMTM5NTYxYzlmZTA2OA==";

    /**
     * 使用默认的key和iv加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
        return encrypt(data, KEY, IV);
    }

    /**
     * 使用默认的key和iv解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data) throws Exception {
        return desEncrypt(data, KEY, IV);
    }

    public static byte[] aesDecrypt(String cipherText, String key, String iv) throws Exception {
        byte[] data = Hex.decodeHex(cipherText.toCharArray());
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key.getBytes(), iv.getBytes());
        return cipher.doFinal(data);
    }

    private static Cipher getCipher(int mode, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        //因为AES的加密块大小是128bit(16byte), 所以key是128、192、256bit无关
        //System.out.println("cipher.getBlockSize()： " + cipher.getBlockSize());

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        cipher.init(mode, secretKeySpec, new IvParameterSpec(iv));

        return cipher;
    }

    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {

        //传给crypto的key、iv要使用base64格式
        byte[] bytes = "dufy20170329java".getBytes();
        String base64Str = Base64.encodeBase64String(bytes);
        System.out.println(base64Str);
//        String key = base64Str;
//        String iv = base64Str;

        String key = "P33MER81XR6vZt8S";
        String iv = "V8PriP3ALO2gmSyy";
        String data = "5bc00657334f0576952c4091bd98b5d6";
//        String data = "hello123456666781";
//        data = encrypt(data, key, iv);
        System.out.println(data);
        System.out.println(desEncrypt(data, key, iv));
    }


    @Test
    public void test2() throws Exception {
        //传给crypto的key、iv要使用base64格式
        byte[] bytes = "dufy20170329java".getBytes();
        String base64Str = Base64.encodeBase64String(bytes);
        System.out.println(base64Str);

        String crypto = "b3bfc61b9fe9cf22aac0a43792930a0b";
        byte[] data = Hex.decodeHex(crypto.toCharArray());
//        byte[] s = aesCbcDecrypt(data, bytes, bytes);
//        System.out.println(new String(s));
    }


}