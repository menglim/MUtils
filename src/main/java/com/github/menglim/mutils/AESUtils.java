package com.github.menglim.mutils;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Slf4j
@UtilityClass
public class AESUtils {
    public String encrypt(String plainText, String keyPhrase) throws Exception {
        if(keyPhrase.length()!=16){
            throw new  Exception("Key must be in 16 digits");
        }
        try {
            Key aesKey = new SecretKeySpec(keyPhrase.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return AppUtils.getInstance().toBase64(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String encryptedBase64Text, String keyPhrase) {
        try {
            Key aesKey = new SecretKeySpec(keyPhrase.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] encryptedText = AppUtils.getInstance().fromBase64ToByte(encryptedBase64Text);
            String decrypted = new String(cipher.doFinal(encryptedText));
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String keyPhrase ="1234567890123456";
        String text= "0040";

        String encrypted = AESUtils.encrypt(text, keyPhrase);
        System.out.println("Encryted => " + encrypted);

        System.out.println("Decrypted => " + AESUtils.decrypt(encrypted, keyPhrase));
    }
}
