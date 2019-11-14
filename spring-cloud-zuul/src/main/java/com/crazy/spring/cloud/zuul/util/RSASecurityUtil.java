package com.crazy.spring.cloud.zuul.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSASecurityUtil {
    /**
     * 指定加密算法为RSA
     */
    private static final String ALGORITHM = "RSA";
    /**
     * 密钥长度，用来初始化
     */
    private static final int KEYSIZE = 1024;
    /**
     * 指定公钥存放文件
     */
    private static String PUBLIC_KEY_FILE = "PublicKey";
    /**
     * 指定私钥存放文件
     */
    private static String PRIVATE_KEY_FILE = "PrivateKey";

    /**
     * 生成密钥对
     *
     * @throws Exception
     */
    private static void generateKeyPair() throws Exception {

        // /** RSA算法要求有一个可信任的随机数源 */
        // SecureRandom secureRandom = new SecureRandom();

        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        // keyPairGenerator.initialize(KEYSIZE, secureRandom);
        keyPairGenerator.initialize(KEYSIZE);

        /** 生成密匙对 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        /** 得到公钥 */
        Key publicKey = keyPair.getPublic();

        String publicKeyStr = Hex.encodeHexString(publicKey.getEncoded());
        System.out.println("公钥key:\n" + publicKeyStr);

        /** 得到私钥 */
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Hex.encodeHexString(privateKey.getEncoded());
        System.out.println("私钥key\n" + privateKeyStr);

        ObjectOutputStream oos1 = null;
        ObjectOutputStream oos2 = null;
        try {
            /** 用对象流将生成的密钥写入文件 */
            oos1 = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
            oos2 = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
            oos1.writeObject(publicKey);
            oos2.writeObject(privateKey);
        } catch (Exception e) {
            throw e;
        } finally {
            /** 清空缓存，关闭文件输出流 */
            oos1.close();
            oos2.close();
        }
    }

    /**
     * 公钥加密方法
     *
     * @param source 源数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String source) throws Exception {
        Key publicKey;
        ObjectInputStream ois = null;
        try {
            // 将文件中的公钥对象读出
            ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            publicKey = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            ois.close();
        }

        // 得到Cipher对象来实现对源数据的RSA加密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] b = source.getBytes();
        // 执行加密操作
        byte[] b1 = cipher.doFinal(b);
        return Base64.encodeBase64String(b1);
    }


    /**
     * 私钥加密方法
     *
     * @param source 源数据
     * @return
     * @throws Exception
     */
    public static String encryptPriKey(String source) throws Exception {
        Key privateKey;
        ObjectInputStream ois = null;
        try {
            //  将文件中的私钥对象读出
            ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            privateKey = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            ois.close();
        }

        // 得到Cipher对象来实现对源数据的RSA加密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        // 执行加密操作
        byte[] b1 = cipher.doFinal(source.getBytes());
        return Base64.encodeBase64String(b1);
    }

    /**
     * 私钥解密算法
     *
     * @param cryptograph 密文
     * @return
     * @throws Exception
     */
    public static String decrypt(String cryptograph) throws Exception {
        Key privateKey;
        ObjectInputStream ois = null;
        try {
            /** 将文件中的私钥对象读出 */
            ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            privateKey = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            ois.close();
        }

        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] b1 = Base64.decodeBase64(cryptograph);

        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

    /**
     * 公钥解密算法
     *
     * @param cryptograph 密文
     * @return
     * @throws Exception
     */
    public static String decryptPubKey(String cryptograph) throws Exception {
        Key publicKey;
        ObjectInputStream ois = null;
        try {
            /** 将文件中的公钥钥对象读出 */
            ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            publicKey = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            ois.close();
        }

        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] b1 = Base64.decodeBase64(cryptograph);

        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

    /**
     * 私钥加密
     * @param sourceContent 明文
     * @param privateKeyStr 私钥
     * @return 密文
     * @throws Exception e
     */
    public static String encryptByPriKey(String sourceContent, String privateKeyStr) throws Exception {
        // 处理密钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec
                = new PKCS8EncodedKeySpec(Hex.decodeHex(privateKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        // 初始化加密器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(sourceContent.getBytes());
        return Hex.encodeHexString(bytes);
    }

    /**
     * 公钥加密
     * @param sourceContent 明文
     * @param publicKeyStr 公钥
     * @return 密文
     * @throws Exception e
     */
    public static String encryptByPubKey(String sourceContent, String publicKeyStr) throws Exception {
        // 处理密钥
        X509EncodedKeySpec x509EncodedKeySpec
                = new X509EncodedKeySpec(Hex.decodeHex(publicKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        // 初始化加密器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(sourceContent.getBytes());
        return Hex.encodeHexString(bytes);
    }

    /**
     * 公钥解密
     * @param sourceContent 密文
     * @param publicKeyStr 公钥
     * @return 明文
     * @throws Exception e
     */
    public static String decryptByPubKey(String sourceContent, String publicKeyStr) throws Exception {
        // 处理密钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Hex.decodeHex(publicKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        // 加密器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(Hex.decodeHex (sourceContent));
        // 这里要用 new String
        return new String(bytes);
    }

    /**
     * 私钥解密
     * @param sourceContent 密文
     * @param privateKeyStr 私钥
     * @return 明文
     * @throws Exception e
     */
    public static String decryptByPriKey(String sourceContent, String privateKeyStr) throws Exception {
        // 处理密钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Hex.decodeHex(privateKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        // 加密器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(Hex.decodeHex (sourceContent));
        // 这里要用 new String
        return new String(bytes);
    }

    public static void main(String[] args) throws Exception {
        test2();

    }

    /**
     * 通过写入文件加密
     *
     * @throws Exception
     */
    private static void test() throws Exception {
        generateKeyPair();//生成密钥对文件
        String source = "恭喜发财!";// 要加密的字符串
        System.out.println("准备用公钥加密的字符串为：" + source);

        String cryptograph = encrypt(source);// 生成的密文
        System.out.print("用公钥加密后的结果为:" + cryptograph);
        System.out.println();

        String target = decrypt(cryptograph);// 解密密文
        System.out.println("用私钥解密后的字符串为：" + target);
        System.out.println();


        System.out.println("准备用私钥加密的字符串为：" + source);

        String cryptograph2 = encryptPriKey(source);// 生成的密文
        System.out.print("用私钥加密后的结果为:" + cryptograph2);
        System.out.println();

        String target2 = decryptPubKey(cryptograph2);// 解密密文
        System.out.println("用公钥解密后的字符串为：" + target2);
        System.out.println();
    }

    private static void test2() throws Exception {
        String pubKey = "30819f300d06092a864886f70d010101050003818d0030818902818100a0b5ba4fd114bd48ad9738cbc8436a790e8a49145cb7c1cf82ca8081ac5eec553fdb945b2ae90a9e95cc51e9e2bdfc2b90608b63ee72f2d2877f510bfb742225c605249aff7f291ef971ef1474001ce1127b6a7468b9931dc709ac181c5a3b790cb6a7394c8fc6d478812030fcb9c30ea3726ffb2741464df16a1c706e34139d0203010001";
        String priKey = "30820276020100300d06092a864886f70d0101010500048202603082025c02010002818100a0b5ba4fd114bd48ad9738cbc8436a790e8a49145cb7c1cf82ca8081ac5eec553fdb945b2ae90a9e95cc51e9e2bdfc2b90608b63ee72f2d2877f510bfb742225c605249aff7f291ef971ef1474001ce1127b6a7468b9931dc709ac181c5a3b790cb6a7394c8fc6d478812030fcb9c30ea3726ffb2741464df16a1c706e34139d02030100010281803415b1a45ca6d411d1f0c8bf82d01699d5548c0ac561e8b650354120c5b49df3a3168f265ce9da3f3b5d54cef065926cac061e26ed08e419c740440c161ab555f4a37fe075907ad5d855eef9cd0d39db66f7879acfedfc862bd441f7aaaa08f054af7b3ee7f98b7b2b557830d53af134e972036f0cfc5a2100f1c220dad0fa01024100dea47d2275ecf0061b97f64071f07a4ad294bd206c463ac29f5373da1e4dcb32a06d86e7192b279b6d21c69a2c1b42fcd636df961be7f0a1d70cb5e8e32091cd024100b8c9cbc63014949f24de6a6a9207d68cfbb20c467e20d9ccedbba55829cdb8901e2fabf598d2ca5ced795764522a23504046e20d1a40daab7cc73c128622f911024100b0b65bffeb1b89933f7988e1b3cdcc32f11b4f5599bad04ec348e88e3a3942cbc71bc7d44b5cbe4e15fb95f7ae8460d1ef70945c3f8003b560cbdce2a0cbac0d0240675531065acca76f7706892b9f0d9304b39b15123665d865a86b4e42c6ddab5fac01ac7f623e3dbb49709956183c4813105e92fa10bc438bb1f317bca415da8102400c5400f8d86b0b57d40b3d0e65601fe88567e962e5f2351d9d447c1e629636adc430c4bc5463c013224654eb8a75e700d5d2d467b1d0b0b1a0eb92bacf60c76c";
        String content = "你好，世界";

        String encryptText = encryptByPriKey(content, priKey);
       // System.out.println("用私钥加密后的字符串为：" + encryptText);
        System.out.println();

        String decrypt = decryptByPubKey(encryptText, pubKey);
        System.out.println("用公钥解密后的字符串为：" + decrypt);
        System.out.println();

        String pubEncryptText = encryptByPubKey(content, pubKey);
        // System.out.println("用私钥加密后的字符串为：" + encryptText);
        System.out.println();

        String priEncryptText = decryptByPriKey(pubEncryptText, priKey);
        System.out.println("用私钥解密后的字符串为：" + priEncryptText);
        System.out.println();


    }
}
