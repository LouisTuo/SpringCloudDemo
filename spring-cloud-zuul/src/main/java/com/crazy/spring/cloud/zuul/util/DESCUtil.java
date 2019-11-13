package com.crazy.spring.cloud.zuul.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author Louis
 * @description： desc加密算法
 * @create 2019-11-12 23:03
 */
public class DESCUtil {

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        String context = "你好，加密喔";
        String originKey = "1234569";
        String encript = encrypt(context, originKey);
        System.out.println("加密后字符串：" + encript);
        String decrypt = decrypt(encript, originKey);
        System.out.println("解密后字符串：" + decrypt);
    }

    /**
     * 加密运算：通过对比特位进行一系列数学运算
     *
     * @param contect   明文
     * @param originKey 密钥，长度64位，8个字节
     * @return 密文
     */
    public static String encrypt(String contect, String originKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        // 1. 获取加密算法工具类对象
        Cipher cipher = Cipher.getInstance("DES");
        // 2. 对加密工具类对象进行初始化
        SecretKeySpec secretKeySpec = getKey(originKey);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        // 3. 用加密工具类对明文进行加密
        byte[] doFinal = cipher.doFinal(contect.getBytes());
        //return new String(bytes,"utf-8");
        return Base64.getEncoder().encodeToString(doFinal);
    }

    /**
     * 不论originKey长度多少，始终要生成一个8个字节长度的原始密钥
     *
     * @param originKey 原始密钥
     * @return 密钥
     */
    private static SecretKeySpec getKey(String originKey) {
        // byte类型的数组，默认每个值初始值为0 (这里是byte，而不是Byte)
        byte[] buffer = new byte[8];
        byte[] originKeyBytes = originKey.getBytes();
        for (int i = 0; i < 8 && i < originKeyBytes.length; i++) {
            buffer[i] = originKeyBytes[i];
        }
        return new SecretKeySpec(buffer, "DES");
    }

    /**
     * DES解密
     *
     * @param content   密文
     * @param originKey 原始密钥
     * @return 明文
     */
    public static String decrypt(String content, String originKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("DES");
        SecretKeySpec key = getKey(originKey);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decode = Base64.getDecoder().decode(content);
        byte[] doFinal = cipher.doFinal(decode);
        return new String(doFinal);
    }
}
