package Hasher;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
//PBKDF2
public class PasswordHasher {
    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 65536;
        int keyLength = 256;
        byte[] salt = getSalt();

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String password, String stored) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = stored.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Stored password must have the form 'salt:hash'");
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] hash = Base64.getDecoder().decode(parts[1]);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] testHash = factory.generateSecret(spec).getEncoded();

        return java.security.MessageDigest.isEqual(hash, testHash);
    }

    private static byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
