package com.crazy.spring.cloud.zuul.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 加密demo
 */
public class EncryptDemo {
    private static String publicKey = "305c300d06092a864886f70d0101010500034b0030480241008cb146c7c031edc4a4df9c372062eb2d8d4b2dc2f29ad13f4d86e109e8aff585f309acd83c37ce1839edface22535d4e039a9a74893b3a563ac80a2394f67aa90203010001";
    private static String privateKey = "30820154020100300d06092a864886f70d01010105000482013e3082013a0201000241008cb146c7c031edc4a4df9c372062eb2d8d4b2dc2f29ad13f4d86e109e8aff585f309acd83c37ce1839edface22535d4e039a9a74893b3a563ac80a2394f67aa9020301000102410085849baa67a03a885afb86f1ddff6236e9974607735b2f6746f8d0ca29940b8939f32aa3cae06cce80de230eaee488c20db14a24d663eb8cd8bffb4a731a5cf1022100c41bceee037caa263b184042bddecf0433df97707b1b861c116918bce39322a5022100b7a8ea75ff66b992d536e8e8a9c2d536dcfdbae7d544f362a89663fece2e4cb50220698f82729203efdec65e8670a69da1976488cd8ea965a669c3616c04790781550220370659b09ea37547e3af7eda6e3a1c4d42aa963bed2eaa48cdbfb917d74811dd022047343c827fdaa73ea76a7d0017f85f18381462c0f67258a8c20b3aa2e15c1369";

    public static Map<String, Object> encryptParam() throws Exception {
        // 随机生成加密密钥
        String aesKey = UuidUtil.getUuid().substring(0, 16).toUpperCase();
        System.out.println("随机生成的密钥" + aesKey);
        // 需要脱敏的字段
        String[] keys = new String[]{"mobile", "password"};

        // 组装参数
        Map<String, Object> params = new HashMap<>();
        params.put("requestId", String.valueOf(System.currentTimeMillis()));
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("mobile", "13307193141");
        params.put("password", "test");

        // 对加密密钥进行加密
        params.put("encodeKey", SecurityUitl.encode(aesKey, EncryptDemo.publicKey));

        // 对敏感字段加密
        HashMap<String, Object> map = new HashMap<>();
        for (String key : keys) {
            map.put(key, params.get(key));
            params.remove(key);
        }
        String fieldVals = null;
        if (!map.isEmpty()) {
            fieldVals = JSON.toJSONString(map);
        }
        params.put("fieldVals", fieldVals);
        // 获取要签名的字符串
        String signStr = getSignStr(params);
        System.out.println("待签名的字符串:\n" + signStr);
        // 签名
        String signature = HMACSHA1.hmacSHA1EncryptToString(signStr, aesKey);
        params.put("signature", signature);
        System.out.println("加密时生成签名：\n" + signature);
        // 对敏感信息加密
        params.put("encodeFieldVals", SensitiveInfoUtils.encode(fieldVals, aesKey));
        params.remove("fieldVals");
        return params;

    }

    // 将入参按照key排序后进行拼接，格式为：（value1：key1|value2:key2|...）拼接后的得到签名source
    private static String getSignStr(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        if (map == null || map.isEmpty()) {
            return sb.toString();
        }
        map = sortMapByKey(map);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!"signature".equals(entry.getKey())) {
                sb.append("|");
                sb.append(entry.getValue());
                sb.append(":");
                sb.append(entry.getKey());
            }
        }
        String result = sb.toString();
        if (result.startsWith("|")) {
            result = result.substring(1);
        }
        if (StringUtils.isNotBlank(result)) {
            result = "(" + result + ")";
        }
        return result;
    }

    private static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }


    private static Map<String, Object> decode(Map<String, Object> map) throws Exception {
        String encodeFieldVals = (String) map.get("encodeFieldVals");
        String fieldVals = null;
        String aesKey = null;
        if (StringUtils.isNotBlank(encodeFieldVals)) {
            aesKey = getAesKey(map);
            if (StringUtils.isBlank(aesKey)) {
                System.out.println("aesKey is null");
                return null;
            }
            fieldVals = SensitiveInfoUtils.decode(encodeFieldVals, aesKey);
            if (fieldVals == null) {
                System.out.println("--decode fieldVals is null--");
                return null;
            }
            // 解密后的业务参数json，放在参数map中，后面签名验证需要用到
            map.remove("encodeFieldVals");
            map.put("fieldVals", fieldVals);
        }
        // 根据入参，获取签名source
        String signStr = getSignStr(map);
        System.out.println("解密：根据入参获得的source" + signStr);

        // 获得签名
        if (StringUtils.isNotBlank(signStr)) {
            String sign = HMACSHA1.hmacSHA1EncryptToString(signStr, aesKey);
            String signature = (String) map.get("signature");
            if (StringUtils.isBlank(sign) || StringUtils.isBlank(signature) ||
                    !sign.equalsIgnoreCase(signature)) {
                System.out.println("Security Validate failed");
                return null;
            }
        }
        // 将业务参数打散到request中
        if (StringUtils.isNotBlank(fieldVals) && JSON.parse(fieldVals) != null) {
            Object o = JSON.parse(fieldVals);
            map.remove("fieldVals");
            if (o instanceof JSONObject) {
                for (Map.Entry<String, Object> entry : ((JSONObject) o).entrySet()) {
                    if (entry.getValue() instanceof JSONArray) {
                        map.put(entry.getKey(), entry.getValue());
                    } else if (entry.getValue() instanceof JSONObject) {
                        map.put(entry.getKey(), entry.getValue());
                    } else {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return map;
    }

    private static String getAesKey(Map<String, Object> map) {
        String aesKey = null;
        String paramKey = (String) map.get("encodeKey");
        if (StringUtils.isNotBlank(paramKey)) {
            try {
                aesKey = SecurityUitl.decode(paramKey, EncryptDemo.privateKey);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("!!!!失败!!!!" + e.toString());
            }
        }
        return aesKey;
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> params = EncryptDemo.encryptParam();
        System.out.println("*********");
        System.out.println("加密后：" + JSON.toJSONString(params));
        System.out.println("*********");
        params = EncryptDemo.decode(params);
        System.out.println("解密后" + JSON.toJSONString(params));
        // getkey();
    }

    private static void getkey()
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        SecureRandom rand = new SecureRandom();
        KeyPairGenerator gen = null;
        try {
            gen = KeyPairGenerator.getInstance("DSA");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        gen.initialize(1024, rand);
        KeyPair keys = gen.generateKeyPair();
        PublicKey pubkey = keys.getPublic();
        System.out.println("公钥" + pubkey.toString());
        PrivateKey privkey = keys.getPrivate();
        System.out.println("私钥" + privkey.toString());


    }
}
