package com.crazy.spring.cloud.zuul.util;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUitl {

    private static final String KEY_ALOGRITHM = "RSA";
    private static final char[] bcdLookup = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public static String encode(String str, String s_publicKey) {
        if(str == null || s_publicKey == null) {
            return null;
        }
        try {
            RSAPublicKey publicKey = getPublicKey(s_publicKey);
            Cipher cipher = Cipher.getInstance(KEY_ALOGRITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(str.getBytes("UTF-8"));
            return formatString(new String(bytesToHexStr(bytes)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decode(String srcPwdForHexStr, String prkForHexStr) throws Exception {
        RSAPrivateKey prk = getPrivateKey(prkForHexStr);
        Cipher cipher = Cipher.getInstance(KEY_ALOGRITHM);
        cipher.init(Cipher.DECRYPT_MODE, prk);
        byte[] bytes = cipher.doFinal(hexStrToBytes(srcPwdForHexStr));
        return new String(bytes, "UTF-8");
    }

    private static RSAPrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] bytes = hexStrToBytes(privateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALOGRITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }

    private static String formatString(String sourceStr) {
        if (sourceStr == null) {
            return null;
        }
        return sourceStr.replaceAll("\\r","").replace("\\n", "");
    }

    public static RSAPublicKey getPublicKey(String publicKey) throws Exception {
        byte[] bytes = hexStrToBytes(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALOGRITHM);
        return (RSAPublicKey) keyFactory.generatePublic(spec);

    }

    /**
     * transform the specified byte into a Hex String form
     * @param bcd
     * @return
     */
    public static String bytesToHexStr(byte[] bcd) {
        StringBuffer stringBuffer = new StringBuffer(bcd.length * 2);
        for (int i = 0; i < bcd.length; i++) {
            stringBuffer.append(bcdLookup[(bcd[i] >>> 4) & 0x0f]);
            stringBuffer.append(bcdLookup[bcd[i] & 0x0f]);
        }
        return stringBuffer.toString();
    }

    // TODO: 2019/9/28 不懂这的意思
    // transform the specified Hex String into a byte array
    public static byte[] hexStrToBytes(String s) {
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2*i, 2*i +2), 16);
        }
        return bytes;
    }
}
