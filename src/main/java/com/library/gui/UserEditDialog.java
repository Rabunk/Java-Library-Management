package com.library.gui;

import com.library.dao.UserDAO;
import com.library.model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserEditDialog extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JButton saveButton;
    private JButton cancelButton;
    private User user;
    private UserDAO userDAO;
    private boolean success = false;

    public UserEditDialog(Window parent, User user) {
        super((JFrame) parent, "Quản lý Độc Giả", true);
        this.user = user;
        this.userDAO = new UserDAO();
        initComponents();
        if (user != null) {
            populateFields();
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(getOwner());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(15, 23, 42));
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel(user == null ? "Thêm độc giả mới" : "Chỉnh sửa độc giả");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Name
        JLabel nameLabel = new JLabel("Tên:");
        nameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setBackground(new Color(30, 40, 60));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setBackground(new Color(30, 40, 60));
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setBackground(new Color(30, 40, 60));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(roleLabel, gbc);

        roleCombo = new JComboBox<>(new String[]{"customer", "admin"});
        roleCombo.setBackground(new Color(30, 40, 60));
        roleCombo.setForeground(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(roleCombo, gbc);

        // Phone
        JLabel phoneLabel = new JLabel("Điện thoại:");
        phoneLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        phoneField.setBackground(new Color(30, 40, 60));
        phoneField.setForeground(Color.WHITE);
        phoneField.setCaretColor(Color.WHITE);
        phoneField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 1;
        mainPanel.add(phoneField, gbc);

        // Address
        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(addressLabel, gbc);

        addressArea = new JTextArea(3, 20);
        addressArea.setBackground(new Color(30, 40, 60));
        addressArea.setForeground(Color.WHITE);
        addressArea.setCaretColor(Color.WHITE);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(addressArea);
        gbc.gridx = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(15, 23, 42));
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));

        saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> save());
        buttonPanel.add(saveButton);

        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
    }

    private void populateFields() {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        roleCombo.setSelectedItem(user.getRole());
        phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
        addressArea.setText(user.getAddress() != null ? user.getAddress() : "");
        emailField.setEditable(false);
        passwordField.setEnabled(false);
    }

    private void save() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đủ thông tin", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (user == null) {
                // Add new user
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                User newUser = new User(name, email, password, role);
                newUser.setPhone(phone.isEmpty() ? null : phone);
                newUser.setAddress(address.isEmpty() ? null : address);
                newUser.setCreatedAt(LocalDateTime.now());
                newUser.setUpdatedAt(LocalDateTime.now());
                
                if (userDAO.addUser(newUser)) {
                    JOptionPane.showMessageDialog(this, "Thêm độc giả thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    success = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm độc giả thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing user
                user.setName(name);
                user.setRole(role);
                user.setPhone(phone.isEmpty() ? null : phone);
                user.setAddress(address.isEmpty() ? null : address);
                user.setUpdatedAt(LocalDateTime.now());

                if (userDAO.updateUser(user)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật độc giả thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    success = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật độc giả thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
