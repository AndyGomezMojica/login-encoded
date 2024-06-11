package com.andygomez.register_login.flow.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class TokenUseCase {

    @Value("${key.public-key}")
    private String publicKey;

    @Value("${key.private-key}")
    private String privateKey;

    public String encrypt(String message) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            javax.crypto.SecretKey aesKey = keyGenerator.generateKey();

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedData = aesCipher.doFinal(message.getBytes());

            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
            byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());

            byte[] combinedData = new byte[encryptedAesKey.length + encryptedData.length];
            System.arraycopy(encryptedAesKey, 0, combinedData, 0, encryptedAesKey.length);
            System.arraycopy(encryptedData, 0, combinedData, encryptedAesKey.length, encryptedData.length);

            return encode(combinedData);
        } catch (Exception e) {
            log.error("Error to encrypt", e);
            throw new RuntimeException("Error to encrypt", e);
        }
    }

    public String decrypt(String encryptedMessage) {
        try {
            byte[] encryptedBytes = decode(encryptedMessage);

            byte[] encryptedAesKey = new byte[128];
            System.arraycopy(encryptedBytes, 0, encryptedAesKey, 0, 128);
            byte[] encryptedData = new byte[encryptedBytes.length - 128];
            System.arraycopy(encryptedBytes, 128, encryptedData, 0, encryptedData.length);

            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
            javax.crypto.SecretKey aesKey = new SecretKeySpec(rsaCipher.doFinal(encryptedAesKey), "AES");

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedMessage = aesCipher.doFinal(encryptedData);

            return new String(decryptedMessage, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error to decrypt", e);
            throw new RuntimeException("Error to decrypt", e);
        }
    }

    private PublicKey getPublicKey(String publicKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private PrivateKey getPrivateKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
