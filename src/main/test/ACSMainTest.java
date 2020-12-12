import com.github.menglim.mutils.AESUtils;

public class ACSMainTest {
    public static void main(String[] args) throws Exception {
        String keyPhrase = "1234567890123456";
        String text = "0040";

        String encrypted = AESUtils.encrypt(text, keyPhrase);
        System.out.println("Encrypted => " + encrypted);

        System.out.println("Decrypted => " + AESUtils.decrypt(encrypted, keyPhrase));
    }
}
