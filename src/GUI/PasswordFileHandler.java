package GUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Hasher.FileEncryptor;

public class PasswordFileHandler {
    private String userFile;
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "MySuperSecretKey".getBytes();

    public PasswordFileHandler(String nickname) {
        this.userFile = nickname + "_passwords.csv";
    }

    public void loadUserFile() {
        try {
            File userFile = new File(this.userFile);
            if (userFile.exists()) {
                FileEncryptor.decryptFile(userFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void encryptUserFile() {
        try {
            File userFile = new File(this.userFile);
            if (userFile.exists()) {
                FileEncryptor.encryptFile(userFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean savePassword(String name, String password, String url, String comment) {
        try (FileWriter writer = new FileWriter(userFile, true)) {
            writer.append(name)
                  .append(",")
                  .append(encrypt(password))
                  .append(",")
                  .append(url)
                  .append(",")
                  .append(comment)
                  .append("\n");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String searchPassword(String name, String url) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(userFile));
            for (String line : lines) {
                String[] parts = line.split(",");
                boolean nameMatches = !name.isEmpty() && parts[0].equalsIgnoreCase(name);
                boolean urlMatches = !url.isEmpty() && parts[2].equalsIgnoreCase(url);
                if (nameMatches || urlMatches) {
                    return "Name: " + parts[0] + "\nPassword: " + parts[1] + "\nURL/App: " + parts[2] + "\nComment: " + parts[3];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public boolean updatePassword(String name, String newPassword) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(userFile));
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts[0].equalsIgnoreCase(name)) {
                    updatedLines.add(parts[0] + "," + encrypt(newPassword) + "," + parts[2] + "," + parts[3]);
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }
            if (found) {
                Files.write(Paths.get(userFile), updatedLines);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePassword(String name) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(userFile));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.split(",")[0].equalsIgnoreCase(name))
                    .collect(Collectors.toList());
            Files.write(Paths.get(userFile), updatedLines);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String encrypt(String password) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String encryptedPassword) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
