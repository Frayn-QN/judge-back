package com.qingniao.judge.util;


import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.service.redis.RedisAccess;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
@AllArgsConstructor
public class PasswordUtil {
    private RedisAccess redisAccess;// 将公钥私钥放入Redis缓存

    private static final int KEY_SIZE = 1024;// 密钥大小

    @PostConstruct
    public void generateKey() throws Exception{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(KEY_SIZE);
        KeyPair keyPair = keyGen.generateKeyPair();

        // Base64二进制转换为字符串
        String privateKeyStr = Base64.getEncoder()
                .encodeToString(keyPair.getPrivate().getEncoded());
        String publicKeyStr = Base64.getEncoder()
                .encodeToString(keyPair.getPublic().getEncoded());

        // 持久化到Redis
        redisAccess.set("public_key", publicKeyStr);
        redisAccess.set("private_key", privateKeyStr);
    }

    public String getPublicKeyStr() {
        return (String) redisAccess.get("public_key");
    }

    private PrivateKey getPrivateKey() throws Exception{
        String privateKeyStr = (String) redisAccess.get("private_key");
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    private String decryptWithPrivate(String encryptText) throws Exception{// 私钥解密
        Cipher cipher = Cipher.getInstance("RSA");
        PrivateKey privateKey = this.getPrivateKey();
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedText = cipher.doFinal(Base64.getDecoder().decode(encryptText));
        return new String(decryptedText);
    }



    private String hashPassword(String password) throws Exception{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(encodedHash);
    }

    public String dealPassword(String input) {
        try {
            String decryptInput = this.decryptWithPrivate(input);// 私钥解密
            return this.hashPassword(decryptInput);
        }
        catch (Exception e) {
            throw new BusinessException("Password", e);
        }
    }
}
