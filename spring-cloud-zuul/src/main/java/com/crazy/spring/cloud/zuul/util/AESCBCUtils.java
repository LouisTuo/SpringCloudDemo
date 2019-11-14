package com.crazy.spring.cloud.zuul.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * AES加密解密字符串工具类
 * 概述：AES高级加密标准，是对称密钥加密中最流行的算法之一；
 * 工作模式包括：ECB、CBC、CTR、OFB、CFB；
 * 使用范围：该工具类仅支持CBC模式下的：
 * 填充：PKCS7PADDING
 * 数据块：128位
 * 密码（key）：32字节长度（例如：12345678901234567890123456789012）
 * 偏移量（iv）：16字节长度（例如：1234567890123456）
 * 输出：hex
 * 字符集：UTF-8
 * 使用方式：String encrypt = AESCBCUtil.encrypt("wy");
 * String decrypt = AESCBCUtil.decrypt(encrypt);
 * 验证方式：http://tool.chacuo.net/cryptaes（在线AES加密解密）
 */
@Slf4j
public class AESCBCUtils {

    // 算法类型：用于指定生成AES的密钥
    private static String ALGORITHM = "AES";
    //AES 设计有三个密钥长度：128，192，256 位
    private static String KEY = "AD42F6697B035B7580E4FEF93BE20BAD";

    // iv偏移量
    private static final String IV = "1234567890123456";

    // 偏移量
    private static final int OFFSET = 16;

    private static final String CHARSET = "utf-8";

    // 加密器类型:加密算法为AES,加密模式为CBC,补码方式为PKCS5Padding
    private static String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static String encrypt(String content) {
        return encrypt(content, KEY);
    }

    public static String decrypt(String content) {
        return decrypt(content, KEY);
    }

    /**
     * 加密：对字符串进行加密，并返回十六进制字符串（hex）
     *
     * @param content 需要加密的字符串
     * @param key     密钥
     * @return 异常
     */
    public static String encrypt(String content, String key) {
        if (content == null) {
            log.error("AES with CBC加密Failed,encryptStr is null");
            return null;
        }
        try {
            // 构造密钥
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET), ALGORITHM);
            //创建初始向量iv用于指定密钥偏移量(可自行指定但必须为128位)，因为AES是分组加密，下一组的iv就用上一组加密的密文来充当
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(CHARSET), 0, OFFSET);

            // 创建AES加密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            // 使用加密器的加密模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] contentBytes = content.getBytes(CHARSET);
            // 加密
            byte[] result = cipher.doFinal(contentBytes);
            // 使用BASE64对加密后的二进制数组进行编码
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            log.error("加密失败：{}", e.getMessage());
        }
        return null;
    }

    /**
     * AES（256）解密
     *
     * @param content 待解密内容
     * @param key     解密密钥
     * @return 解密之后
     * @throws Exception 异常
     */
    public static String decrypt(String content, String key) {
        try {

            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(), 0, OFFSET);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            //解密时使用加密器的解密模式
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, skey, ivParameterSpec);
            byte[] result = cipher.doFinal(Base64.getDecoder().decode(content));
            // 解密
            return new String(result);
        } catch (Exception e) {
            log.info("解密失败：{}", e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        byte[] bytes = genKey();
        // String base64Key = Base64.getEncoder().encodeToString(bytes);
        String base64Key = Hex.encodeHexString(bytes);

        System.out.println("动态生成的key："+ base64Key + "，长度：" + base64Key.length());
        String content = "123";
        System.out.println("待加密的字符串:" + content);
        System.out.println("----加密后----");
        String encrypt = AESCBCUtils.encrypt(content, base64Key);
        // 也可以使用固定 key
        // String encrypt = AESCBCUtils.encrypt(content);
        System.out.println(encrypt);
        System.out.println("----解密后----");
        String decrypt = AESCBCUtils.decrypt(encrypt, base64Key);
        System.out.println(decrypt);
    }

    /**
     * 使用KeyGenerator生成key
     * @return 密钥
     */
    public static byte[] genKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            // size
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[1];
    }
}
