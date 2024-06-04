package Hasher;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileEncryptor {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 128;
    private static final byte[] KEY = "12345678901234567890123456789012".getBytes(); 

    public static void encryptFile(File file) throws Exception {
        byte[] content = Files.readAllBytes(file.toPath());
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
        byte[] iv = new byte[IV_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        byte[] encryptedContent = cipher.doFinal(content);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(iv);
            fos.write(encryptedContent);
        }
    }

    public static void decryptFile(File file) throws Exception {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(fileContent, 0, iv, 0, IV_SIZE);
        byte[] encryptedContent = new byte[fileContent.length - IV_SIZE];
        System.arraycopy(fileContent, IV_SIZE, encryptedContent, 0, encryptedContent.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        byte[] decryptedContent = cipher.doFinal(encryptedContent);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(decryptedContent);
        }
    }
}
