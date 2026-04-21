package com.library.gui;

import com.library.dao.ProductDAO;
import com.library.dao.CategoryDAO;
import com.library.model.Product;
import com.library.model.Category;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductEditDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JTextField originalPriceField;
    private JTextField stockField;
    private JTextField materialField;
    private JComboBox<Category> categoryCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private Product product;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private boolean success = false;

    public ProductEditDialog(Window parent, Product product, CategoryDAO categoryDAO) {
        super((JFrame) parent, "Quản lý Sách", true);
        this.product = product;
        this.categoryDAO = categoryDAO;
        this.productDAO = new ProductDAO();
        initComponents();
        if (product != null) {
            populateFields();
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 600);
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
        JLabel titleLabel = new JLabel(product == null ? "Thêm sách mới" : "Chỉnh sửa sách");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Name
        JLabel nameLabel = new JLabel("Tên sách:");
        nameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setBackground(new Color(30, 40, 60));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Category
        JLabel categoryLabel = new JLabel("Thể loại:");
        categoryLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(categoryLabel, gbc);

        categoryCombo = new JComboBox<>();
        categoryCombo.setBackground(new Color(30, 40, 60));
        categoryCombo.setForeground(Color.WHITE);
        loadCategories();
        gbc.gridx = 1;
        mainPanel.add(categoryCombo, gbc);

        // Material (Author)
        JLabel materialLabel = new JLabel("Tác giả:");
        materialLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(materialLabel, gbc);

        materialField = new JTextField(20);
        materialField.setBackground(new Color(30, 40, 60));
        materialField.setForeground(Color.WHITE);
        materialField.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(materialField, gbc);

        // Price
        JLabel priceLabel = new JLabel("Giá:");
        priceLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(priceLabel, gbc);

        priceField = new JTextField(20);
        priceField.setBackground(new Color(30, 40, 60));
        priceField.setForeground(Color.WHITE);
        priceField.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(priceField, gbc);

        // Original Price
        JLabel originalPriceLabel = new JLabel("Giá gốc:");
        originalPriceLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(originalPriceLabel, gbc);

        originalPriceField = new JTextField(20);
        originalPriceField.setBackground(new Color(30, 40, 60));
        originalPriceField.setForeground(Color.WHITE);
        originalPriceField.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(originalPriceField, gbc);

        // Stock
        JLabel stockLabel = new JLabel("Tồn kho:");
        stockLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(stockLabel, gbc);

        stockField = new JTextField(20);
        stockField.setBackground(new Color(30, 40, 60));
        stockField.setForeground(Color.WHITE);
        stockField.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(stockField, gbc);

        // Description
        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        mainPanel.add(descriptionLabel, gbc);

        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setBackground(new Color(30, 40, 60));
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setCaretColor(Color.WHITE);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        mainPanel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(15, 23, 42));
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));

        saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(34, 197, 94));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> save());
        buttonPanel.add(saveButton);

        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(239, 68, 68));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            for (Category cat : categories) {
                categoryCombo.addItem(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateFields() {
        nameField.setText(product.getName());
        materialField.setText(product.getMaterial() != null ? product.getMaterial() : "");
        priceField.setText(String.valueOf(product.getPrice()));
        originalPriceField.setText(product.getOriginalPrice() != null ? String.valueOf(product.getOriginalPrice()) : "");
        stockField.setText(String.valueOf(product.getStock()));
        descriptionArea.setText(product.getDescription() != null ? product.getDescription() : "");
        
        try {
            Category cat = categoryDAO.getCategoryById(product.getCategoryId());
            categoryCombo.setSelectedItem(cat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Product p = product != null ? product : new Product();
            p.setName(nameField.getText().trim());
            p.setDescription(descriptionArea.getText().trim());
            p.setPrice(new BigDecimal(priceField.getText().trim()));
            p.setOriginalPrice(new BigDecimal(originalPriceField.getText().trim()));
            p.setStock(Integer.parseInt(stockField.getText().trim()));
            p.setMaterial(materialField.getText().trim());
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            p.setCategoryId(selectedCategory.getId());

            if (product == null) {
                productDAO.addProduct(p);
                JOptionPane.showMessageDialog(this, "Thêm sách thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                productDAO.updateProduct(p);
                JOptionPane.showMessageDialog(this, "Chỉnh sửa sách thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            success = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá và tồn kho hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
