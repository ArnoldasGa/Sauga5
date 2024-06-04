package GUI;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

import Hasher.FileEncryptor;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Base64;
import java.util.Random;

public class PasswordManagerGUI extends JFrame {
    private JTextField nameField;
    private JTextField passwordField;
    private JTextField urlField;
    private JTextField commentField;
    private JButton saveButton;
    private JButton searchButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton generatePasswordButton;
    private JButton showPasswordButton;
    private JButton copyToClipboardButton;
    private JTextArea resultArea;
    private PasswordFileHandler fileHandler;
    private String nickname;

    public PasswordManagerGUI(String nickname) {
        this.nickname = nickname;
        setTitle("Password Manager");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.fileHandler = new PasswordFileHandler(nickname);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savePassword();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchPassword();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePassword();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePassword();
            }
        });

        generatePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateRandomPassword();
            }
        });

        showPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPassword();
            }
        });

        copyToClipboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    File userFile = new File(nickname + "_info.txt");
                    if (userFile.exists()) {
                        FileEncryptor.encryptFile(userFile);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        fileHandler.loadUserFile();
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        nameField = new JTextField(20);
        nameField.setBounds(100, 20, 165, 25);
        panel.add(nameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JTextField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        JLabel urlLabel = new JLabel("URL/App:");
        urlLabel.setBounds(10, 80, 80, 25);
        panel.add(urlLabel);

        urlField = new JTextField(20);
        urlField.setBounds(100, 80, 165, 25);
        panel.add(urlField);

        JLabel commentLabel = new JLabel("Comment:");
        commentLabel.setBounds(10, 110, 80, 25);
        panel.add(commentLabel);

        commentField = new JTextField(20);
        commentField.setBounds(100, 110, 165, 25);
        panel.add(commentField);

        saveButton = new JButton("Save");
        saveButton.setBounds(10, 140, 80, 25);
        panel.add(saveButton);

        searchButton = new JButton("Search");
        searchButton.setBounds(100, 140, 80, 25);
        panel.add(searchButton);

        updateButton = new JButton("Update");
        updateButton.setBounds(190, 140, 80, 25);
        panel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(280, 140, 80, 25);
        panel.add(deleteButton);

        generatePasswordButton = new JButton("Generate");
        generatePasswordButton.setBounds(370, 140, 100, 25);
        panel.add(generatePasswordButton);

        showPasswordButton = new JButton("Show");
        showPasswordButton.setBounds(10, 170, 80, 25);
        panel.add(showPasswordButton);

        copyToClipboardButton = new JButton("Copy");
        copyToClipboardButton.setBounds(100, 170, 80, 25);
        panel.add(copyToClipboardButton);

        resultArea = new JTextArea();
        resultArea.setBounds(10, 200, 460, 150);
        panel.add(resultArea);
    }

    private void savePassword() {
        String name = nameField.getText();
        String password = passwordField.getText();
        String url = urlField.getText();
        String comment = commentField.getText();
        if (fileHandler.savePassword(name, password, url, comment)) {
            JOptionPane.showMessageDialog(null, "Password saved successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Error saving password.");
        }
    }

    private void searchPassword() {
        String name = nameField.getText().trim();
        String url = urlField.getText().trim();
        String result = fileHandler.searchPassword(name, url);
        if (result != null) {
            resultArea.setText(result);
        } else {
            JOptionPane.showMessageDialog(null, "Password not found.");
        }
    }

    private void updatePassword() {
        String name = nameField.getText();
        String password = passwordField.getText();
        if (fileHandler.updatePassword(name, password)) {
            JOptionPane.showMessageDialog(null, "Password updated successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Error updating password.");
        }
    }

    private void deletePassword() {
        String name = nameField.getText();
        if (fileHandler.deletePassword(name)) {
            JOptionPane.showMessageDialog(null, "Password deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Error deleting password.");
        }
    }

    private void generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        passwordField.setText(password.toString());
    }

    private void showPassword() {
        String content = resultArea.getText();
        String encryptedPassword = null;
        for (String line : content.split("\n")) {
            if (line.startsWith("Password: ")) {
                encryptedPassword = line.substring(10).trim();
                break;
            }
        }

        if (encryptedPassword != null) {
            String decryptedPassword = decrypt(encryptedPassword);
            passwordField.setText(decryptedPassword);
            resultArea.setText(content.replace("Password: " + encryptedPassword, "Password: " + decryptedPassword));
        }
    }

    private void copyToClipboard() {
        String password = passwordField.getText();
        StringSelection stringSelection = new StringSelection(password);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        JOptionPane.showMessageDialog(null, "Password copied to clipboard!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PasswordManagerGUI("testUser").setVisible(true);
            }
        });
    }

    public String decrypt(String encryptedPassword) {
        try {
            byte[] KEY = "MySuperSecretKey".getBytes();
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(KEY, "AES");
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
