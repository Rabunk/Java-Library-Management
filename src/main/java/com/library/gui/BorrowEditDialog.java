package com.library.gui;

import com.library.dao.BorrowDAO;
import com.library.dao.UserDAO;
import com.library.dao.ProductDAO;
import com.library.model.Borrow;
import com.library.model.User;
import com.library.model.Product;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class BorrowEditDialog extends JDialog {
    private JComboBox<User> userCombo;
    private JComboBox<Product> productCombo;
    private JSpinner borrowDateSpinner;
    private JSpinner dueDateSpinner;
    private JButton saveButton;
    private JButton cancelButton;
    private Borrow borrow;
    private BorrowDAO borrowDAO;
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private boolean success = false;

    public BorrowEditDialog(Window parent, Borrow borrow, UserDAO userDAO, ProductDAO productDAO) {
        super((JFrame) parent, "Quản lý Mượn / Trả", true);
        this.borrow = borrow;
        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.borrowDAO = new BorrowDAO();
        initComponents();
        loadUsers();
        loadProducts();
        if (borrow != null) {
            populateFields();
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 400);
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
        JLabel titleLabel = new JLabel(borrow == null ? "Thêm bản ghi mượn sách" : "Chỉnh sửa mượn sách");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // User
        JLabel userLabel = new JLabel("Độc giả:");
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(userLabel, gbc);

        userCombo = new JComboBox<>();
        userCombo.setBackground(new Color(30, 40, 60));
        userCombo.setForeground(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(userCombo, gbc);

        // Product
        JLabel productLabel = new JLabel("Sách:");
        productLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(productLabel, gbc);

        productCombo = new JComboBox<>();
        productCombo.setBackground(new Color(30, 40, 60));
        productCombo.setForeground(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(productCombo, gbc);

        // Borrow Date
        JLabel borrowDateLabel = new JLabel("Ngày mượn:");
        borrowDateLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(borrowDateLabel, gbc);

        borrowDateSpinner = new JSpinner(new SpinnerDateModel());
        borrowDateSpinner.setEditor(new JSpinner.DateEditor(borrowDateSpinner, "dd/MM/yyyy HH:mm"));
        gbc.gridx = 1;
        mainPanel.add(borrowDateSpinner, gbc);

        // Due Date
        JLabel dueDateLabel = new JLabel("Hạn trả:");
        dueDateLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(dueDateLabel, gbc);

        dueDateSpinner = new JSpinner(new SpinnerDateModel());
        dueDateSpinner.setEditor(new JSpinner.DateEditor(dueDateSpinner, "dd/MM/yyyy HH:mm"));
        gbc.gridx = 1;
        mainPanel.add(dueDateSpinner, gbc);

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
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                userCombo.addItem(user);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải độc giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                productCombo.addItem(product);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields() {
        try {
            User user = userDAO.getUserById(borrow.getUserId());
            if (user != null) {
                userCombo.setSelectedItem(user);
            }
            
            Product product = productDAO.getProductById(borrow.getProductId());
            if (product != null) {
                productCombo.setSelectedItem(product);
            }
            
            borrowDateSpinner.setValue(java.sql.Timestamp.valueOf(borrow.getBorrowDate()));
            dueDateSpinner.setValue(java.sql.Timestamp.valueOf(borrow.getDueDate()));
            
            userCombo.setEnabled(false);
            productCombo.setEnabled(false);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save() {
        if (userCombo.getSelectedItem() == null || productCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả và sách", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User selectedUser = (User) userCombo.getSelectedItem();
            Product selectedProduct = (Product) productCombo.getSelectedItem();
            
            java.util.Date borrowDate = (java.util.Date) borrowDateSpinner.getValue();
            java.util.Date dueDate = (java.util.Date) dueDateSpinner.getValue();
            
            if (borrow == null) {
                // Add new borrow
                Borrow newBorrow = new Borrow(
                        selectedUser.getId(),
                        selectedProduct.getId(),
                        new java.sql.Timestamp(borrowDate.getTime()).toLocalDateTime(),
                        new java.sql.Timestamp(dueDate.getTime()).toLocalDateTime()
                );
                newBorrow.setCreatedAt(LocalDateTime.now());
                newBorrow.setUpdatedAt(LocalDateTime.now());
                
                if (borrowDAO.addBorrow(newBorrow)) {
                    JOptionPane.showMessageDialog(this, "Thêm bản ghi mượn sách thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    success = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm bản ghi mượn sách thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing borrow
                borrow.setDueDate(new java.sql.Timestamp(dueDate.getTime()).toLocalDateTime());
                borrow.setUpdatedAt(LocalDateTime.now());
                
                if (borrowDAO.updateBorrow(borrow)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật bản ghi mượn sách thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    success = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật bản ghi mượn sách thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
