package risk.engine.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * @Author: X
 * @Date: 2025/3/21 14:09
 * @Version: 1.0
 */
public class CryptoUtils {

    /**
     * MD5 加密（32位小写）
     */
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 加密失败", e);
        }
    }

    /**
     * 生成 DES 密钥
     */
//    public static String generateDesKey() {
//        try {
//            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
//            keyGen.init(56);
//            SecretKey secretKey = keyGen.generateKey();
//            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("DES 密钥生成失败", e);
//        }
//    }

    /**
     * DES 加密
     */
    public static String desEncrypt(String data, String key) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = factory.generateSecret(new DESKeySpec(Base64.getDecoder().decode(key)));
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("DES 加密失败", e);
        }
    }

    /**
     * DES 解密
     */
    public static String desDecrypt(String data, String key) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = factory.generateSecret(new DESKeySpec(Base64.getDecoder().decode(key)));
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("DES 解密失败", e);
        }
    }

    /**
     * 生成 RSA 密钥对（Base64 格式）
     */
    public static KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA 密钥对生成失败", e);
        }
    }

    /**
     * RSA 加密
     */
    public static String rsaEncrypt(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("RSA 加密失败", e);
        }
    }

    /**
     * RSA 解密
     */
    public static String rsaDecrypt(String data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA 解密失败", e);
        }
    }

    /**
     * 获取 RSA 密钥的 Base64 字符串格式
     */
    public static String getBase64Key(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 通过 Base64 字符串解析 RSA 公钥
     */
    public static PublicKey getRsaPublicKey(String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new java.security.spec.X509EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            throw new RuntimeException("RSA 公钥解析失败", e);
        }
    }

    /**
     * 通过 Base64 字符串解析 RSA 私钥
     */
    public static PrivateKey getRsaPrivateKey(String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            throw new RuntimeException("RSA 私钥解析失败", e);
        }
    }

    public static String getDesSecretKey() {
        String key = System.getenv("DES_SECRET_KEY");
        if (key == null || key.isEmpty()) {
            throw new RuntimeException("DES 密钥未配置");
        }
        return Base64.getEncoder().encodeToString(key.getBytes());
    }

    /**
     * 测试
     */
    public static void main(String[] args) {

        String secretKey = getDesSecretKey();
        // MD5
        String md5Hash = md5("HelloWorld");
        System.out.println("MD5: " + md5Hash);

        // DES
        //String desKey = generateDesKey();
        System.out.println(secretKey);
        String desEncrypted = desEncrypt("", secretKey);
        String desDecrypted = desDecrypt(desEncrypted, secretKey);
        System.out.println("DES Key: " + secretKey);
        System.out.println("DES Encrypted: " + desEncrypted);
        System.out.println("DES Decrypted: " + desDecrypted);

        // RSA
        KeyPair rsaKeyPair = generateRsaKeyPair();
        String publicKeyBase64 = getBase64Key(rsaKeyPair.getPublic());
        String privateKeyBase64 = getBase64Key(rsaKeyPair.getPrivate());
        String rsaEncrypted = rsaEncrypt("HelloRSA", rsaKeyPair.getPublic());
        String rsaDecrypted = rsaDecrypt(rsaEncrypted, rsaKeyPair.getPrivate());
        //System.out.println("RSA Public Key: " + publicKeyBase64);
        //System.out.println("RSA Private Key: " + privateKeyBase64);
        //System.out.println("RSA Encrypted: " + rsaEncrypted);
        //System.out.println("RSA Decrypted: " + rsaDecrypted);
    }
}
