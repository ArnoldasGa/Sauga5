package GUI;

import javax.swing.*;
import Hasher.PasswordHasher;
import Hasher.FileEncryptor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserLogin extends JFrame {
    private JTextField nicknameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;

    public UserLogin() {
        setTitle("User Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nickname = nicknameField.getText();
                char[] password = passwordField.getPassword();

                if (!nickname.isEmpty() && password.length > 0) {
                    try {
                        File userFile = new File(nickname + "_info.txt");
                        if (userFile.exists()) {
                            FileEncryptor.decryptFile(userFile);
                            String content = new String(Files.readAllBytes(Paths.get(nickname + "_info.txt")));
                            String[] lines = content.split("\n");
                            if (lines.length < 2) {
                                JOptionPane.showMessageDialog(null, "Invalid user file format.");
                                return;
                            }
                            String storedHashedPassword = lines[1].split(": ")[1].trim();
                            if (PasswordHasher.verifyPassword(new String(password), storedHashedPassword)) {
                                JOptionPane.showMessageDialog(null, "Login successful!");
                                new PasswordManagerGUI(nickname).setVisible(true);
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid password.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "User does not exist.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error logging in.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter both nickname and password.");
                }
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new UserAccountCreation().setVisible(true);
                dispose();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    String nickname = nicknameField.getText();
                    File userFile = new File(nickname + "_info.txt");
                    if (userFile.exists()) {
                        FileEncryptor.encryptFile(userFile);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel nicknameLabel = new JLabel("Nickname:");
        nicknameLabel.setBounds(10, 20, 80, 25);
        panel.add(nicknameLabel);

        nicknameField = new JTextField(20);
        nicknameField.setBounds(100, 20, 165, 25);
        panel.add(nicknameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(100, 80, 150, 25);
        panel.add(createAccountButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UserLogin().setVisible(true);
            }
        });
    }
}
