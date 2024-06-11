package com.andygomez.register_login.flow.application;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class CreateTokenUseCase {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public void generateKeys(String seed) {
        try {
            // Genera una clave basada en el seed
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed.getBytes());
            keyPairGenerator.initialize(1024, secureRandom);

            // Genera el par de claves
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //To generate a specific public and private key with an string
//    public static void main(String[] args) {
//        CreateTokenUseCase createTokenUseCase = new CreateTokenUseCase();
//        createTokenUseCase.generateKeys("test");
//        createTokenUseCase.printKeys();
//    }
//
//    public void printKeys() {
//        System.out.println("Public key\n" + encode(publicKey.getEncoded()));
//        System.out.println("Private key\n" + encode(privateKey.getEncoded()));
//    }

    public String encrypt(String message) throws Exception {
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception {
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage);
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private PrivateKey getPrivateKey() throws Exception {
        String privateKeyStr = encode(privateKey.getEncoded());
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
