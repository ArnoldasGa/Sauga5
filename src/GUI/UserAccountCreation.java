package GUI;

import javax.swing.*;
import Hasher.FileEncryptor;
import Hasher.PasswordHasher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;

public class UserAccountCreation extends JFrame {
    private JTextField nicknameField;
    private JPasswordField passwordField;
    private JButton createAccountButton;

    public UserAccountCreation() {
        setTitle("User Account Creation");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nickname = nicknameField.getText();
                char[] password = passwordField.getPassword();

                if (!nickname.isEmpty() && password.length > 0) {
                    try {
                        String hashedPassword = PasswordHasher.hashPassword(new String(password));

                        File userFile = new File(nickname + "_info.txt");
                        if (userFile.exists()) {
                            JOptionPane.showMessageDialog(null, "Nickname already exists. Please choose another.");
                        } else {
                            try (PrintWriter writer = new PrintWriter(userFile)) {
                                writer.println("Nickname: " + nickname);
                                writer.println("Password: " + hashedPassword);
                            }

                            FileEncryptor.encryptFile(userFile);

                            JOptionPane.showMessageDialog(null, "User account created successfully!");
                            dispose();
                            new UserLogin().setVisible(true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error creating user account.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter both nickname and password.");
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

        createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(10, 80, 150, 25);
        panel.add(createAccountButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UserAccountCreation().setVisible(true);
            }
        });
    }
}
