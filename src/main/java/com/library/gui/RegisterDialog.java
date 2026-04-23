package com.library.gui;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nameField;
    private JTextField phoneField;
    private JButton registerButton;
    private JButton exitButton;
    private User loggedInUser;
    private boolean registerSuccess = false;

    public RegisterDialog(JFrame parent) {
        super(parent, "Đăng Ký Tài Khoản", true);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(15, 23, 42));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Đăng ký tài khoản");
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

        // Name Label
        JLabel nameLabel = new JLabel("Tên đăng nhập");
        nameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        mainPanel.add(nameLabel, gbc);

        // Name Field
        nameField = new JTextField(20);
        nameField.setBackground(new Color(30, 40, 60));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(nameField, gbc);

        // Phone Label
        JLabel phoneLabel = new JLabel("Số điện thoại");
        phoneLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        mainPanel.add(phoneLabel, gbc);

        // Phone Field
        phoneField = new JTextField(30);
        phoneField.setBackground(new Color(30, 40, 60));
        phoneField.setForeground(Color.WHITE);
        phoneField.setCaretColor(Color.WHITE);
        phoneField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(phoneField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setBackground(new Color(30, 40, 60));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 0;
        gbc.gridy = 8;
        mainPanel.add(passwordField, gbc);

        // Confirm Password Label
        JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu");
        confirmPasswordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 9;
        mainPanel.add(confirmPasswordLabel, gbc);

        // Confirm Password Field
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBackground(new Color(30, 40, 60));
        confirmPasswordField.setForeground(Color.WHITE);
        confirmPasswordField.setCaretColor(Color.WHITE);
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 0;
        gbc.gridy = 10;
        mainPanel.add(confirmPasswordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(15, 23, 42));
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));

        registerButton = new JButton("Đăng ký");
        registerButton.setBackground(new Color(59, 130, 246));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.addActionListener(e -> register());
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role, phone, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash()); 
            pstmt.setString(4, user.getRole()); 
            pstmt.setString(5, user.getPhone());
            return pstmt.executeUpdate() > 0;
        }
    }

    private void register() {
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        // 1. Kiểm tra trống
        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!");
            return;
        }

        // 2. Kiểm tra khớp mật khẩu
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            // 3. Kiểm tra email đã tồn tại chưa
            if (userDAO.getUserByEmail(email) != null) {
                JOptionPane.showMessageDialog(this, "Email này đã được đăng ký!");
                return;
            }

            // 4. Tạo đối tượng User và lưu
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPasswordHash(password);
            newUser.setPhone(phone);
            newUser.setRole("customer");
            if (userDAO.addUser(newUser)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập.");
                registerSuccess = true;
                dispose(); 
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi DB: " + e.getMessage());
        }
    }

    private void exit() {
        dispose();
        System.exit(0);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isRegisterSuccess() {
        return registerSuccess;
    }
}
