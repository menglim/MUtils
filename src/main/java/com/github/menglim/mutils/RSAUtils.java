package com.github.menglim.mutils;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {

    //    https://www.devglan.com/java8/rsa-encryption-decryption-java
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMzGcJZOLJdLvtTd+rYFGzrscc7PE3+YRHj+SWLh1+sBpvmF7qQFKY3ldSSTBX19q3yEepfax3LCQB7ehTYJKmcty2SU2+t1L4pMvHi98Y5Ks62H36vr9kzC0VB7LG1sKBgcnScvCRQ2fL4yS5prahwEQJIoU4i3h9WZQQO2H3AQIDAQAB";
//    private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAI1fZPWITpBvcnovSVnAGe3DZDsx2wRMpaRCCV2PGmhPxWvVntj5W4sCGxQQPGMjHgxKrKITcZN+5mjjRJG3Nkm2OU8TPuheVQLaOx1DOIqnzeb8TJxNV7KQEFuhwhkNCAzdJNvaRYA4gM/Bms9VE64jBLo2eLuGFRIBtqvGf+UNAgMBAAECgYBKVCKHuY/c/suIlD4Tkx3O1ZFoUhEeFJJuZ033Q9sVhwN8a8dlus03q/bCuLqXQQp2WfikCmcHrovED+GoX1SJ5s1oUwCIXcoXTZuWXU8U1haervr+en4zwrQHqT17MK+n5G4sLqLUslbOpOlOdN/SqfspfW1UQho2q2KoUrAvgQJBAOiQwCo+siyYzT6B7iT8tPRbPQIBHtlTtSmupgnpYvzp1/4TJLW6pWJGoVr2ktHGJrIosILy4g11fK6giRWQIu0CQQCbnjzXHnRkXyGeG+xIWyCAGzhJScrurgkWWiSzN0dan2bwPu88vYeBd5vgoLCTc951CyzFk/fNhlbh1d1ZPeahAkEAmnQP+755zJ6KqPnXtF7UuvihUuR1nXY3egBQq4KByGBxn38NQVg9IECyRtw9c+46otXdLIqsUK33aW6avGRmnQJAEQU9okmycUf2ZVSog0F0TI6tmVZ426ItXER/vxpMs5y7pYs8n2gbag2q/uaUaYxCyIpwWyvT7qbC2fCKRTiAAQJBANAIFqdCJmWCc3pgBacfBb9fbyrUS78MJTWMd17Q4EtwbIFj1d4Rm/6uSeduvwR54DeztwYHbix86+YWd+Hyg6Q=";

    private static RSAUtil _instance;

    public static RSAUtil getInstance() {
        if (_instance == null) _instance = new RSAUtil();
        return _instance;
    }

    public KeyPair generateKeyPair() {

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public String encrypt_short(String rawData, String base64PublicKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(base64PublicKey));
            return Base64.getEncoder().encodeToString(cipher.doFinal(rawData.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encrypt(String message, String publicKeyBase64) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKeyBase64));
            final byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            final int len = bytes.length; // string length
            int offset = 0; // offset
            int i = 0; // number of segments divided by the
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int MAX_ENCRYPT_BLOCK = 117;
            while (len > offset) {
                byte[] cache;
                if (len - offset > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(bytes, offset, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(bytes, offset, len - offset);
                }
                bos.write(cache);
                i++;
                offset = 117 * i;
            }
            bos.close();

            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt_short(byte[] data, PrivateKey privateKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(byte[] data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            final int len = data.length; // ciphertext
            int offset = 0; // offset
            int i = 0; // number of segments
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int MAX_ENCRYPT_BLOCK = 256;
            while (len - offset > 0) {
                byte[] cache;
                if (len - offset > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offset, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offset, len - offset);
                }
                bos.write(cache);
                i++;
                offset = MAX_ENCRYPT_BLOCK * i;
            }
            bos.close();

            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String base64EncryptedData, String base64PrivateKey) {
        if (base64EncryptedData.contains(" ")) {
            base64EncryptedData = base64EncryptedData.replaceAll(" ", "+");
        }
        return decrypt(Base64.getDecoder().decode(base64EncryptedData.getBytes()), getPrivateKey(base64PrivateKey));
    }
}

