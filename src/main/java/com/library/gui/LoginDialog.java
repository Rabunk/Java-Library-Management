package com.library.gui;

import com.library.dao.UserDAO;
import com.library.model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private User loggedInUser;
    private boolean loginSuccess = false;

    public LoginDialog(JFrame parent) {
        super(parent, "Đăng Nhập Hệ Thống", true);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(15, 23, 42));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Đăng nhập hệ thống");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Email Label
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(emailLabel, gbc);

        // Email Field
        emailField = new JTextField(20);
        emailField.setBackground(new Color(30, 40, 60));
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(emailField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setBackground(new Color(30, 40, 60));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(passwordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(15, 23, 42));
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));

        loginButton = new JButton("Đăng nhập");
        loginButton.setBackground(new Color(59, 130, 246));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(e -> login());
        buttonPanel.add(loginButton);

        exitButton = new JButton("Đăng ký");
        exitButton.setBackground(new Color(59, 130, 246));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.addActionListener(e -> openRegister());
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);

            if (user != null && user.getPasswordHash().equals(password)) {
                loggedInUser = user;
                loginSuccess = true;
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email hoặc mật khẩu không đúng!", "Lỗi đăng nhập",
                        JOptionPane.ERROR_MESSAGE);
                emailField.setText("");
                passwordField.setText("");
                emailField.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openRegister() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);

        JFrame parentFrame = (parentWindow instanceof JFrame) ? (JFrame) parentWindow : null;

        RegisterDialog regDialog = new RegisterDialog(parentFrame);
        regDialog.setVisible(true);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}
