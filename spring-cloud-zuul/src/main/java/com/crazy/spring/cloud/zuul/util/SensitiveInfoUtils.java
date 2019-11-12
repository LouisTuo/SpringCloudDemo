package com.crazy.spring.cloud.zuul.util;

/**
 *
 */
public class SensitiveInfoUtils {

    public static String encode(String param, String encodeKey) {
        return AESCBCUtils.encrypt(param, encodeKey);
    }

    public static String decode(String param, String encodeKey) {
        return AESCBCUtils.decrypt(param, encodeKey);
    }


}
