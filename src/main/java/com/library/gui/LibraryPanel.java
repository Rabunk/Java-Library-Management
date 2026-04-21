package com.library.gui;

import com.library.dao.ProductDAO;
import com.library.dao.CategoryDAO;
import com.library.dao.ReviewDAO;
import com.library.model.Product;
import com.library.model.Category;
import com.library.model.Review;
import com.library.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LibraryPanel extends JPanel {
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private ReviewDAO reviewDAO;
    private JComboBox<Category> categoryCombo;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewDetailsButton;
    private User currentUser;

    public LibraryPanel(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
        this.reviewDAO = new ReviewDAO();
        initComponents();
        loadBooks();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 23, 42));

        // Top panel - Search and Filter
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(20, 30, 50));
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Category filter
        JLabel categoryLabel = new JLabel("Thể loại:");
        categoryLabel.setForeground(Color.WHITE);
        topPanel.add(categoryLabel);

        categoryCombo = new JComboBox<>();
        categoryCombo.setBackground(new Color(30, 40, 60));
        categoryCombo.setForeground(Color.WHITE);
        loadCategories();
        categoryCombo.addActionListener(e -> loadBooks());
        topPanel.add(categoryCombo);

        // Search
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setForeground(Color.WHITE);
        topPanel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.setBackground(new Color(30, 40, 60));
        searchField.setForeground(Color.WHITE);
        topPanel.add(searchField);

        searchButton = new JButton("Tìm");
        searchButton.setBackground(new Color(59, 130, 246));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> search());
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // Center - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(15, 23, 42));

        String[] columns = {"ID", "Tên sách", "Tác giả", "Thể loại", "Đơn giá", "Tồn kho", "Đánh giá", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksTable = new JTable(tableModel);
        booksTable.setBackground(new Color(20, 30, 50));
        booksTable.setForeground(Color.WHITE);
        booksTable.getTableHeader().setBackground(new Color(30, 40, 60));
        booksTable.getTableHeader().setForeground(Color.WHITE);
        booksTable.setRowHeight(25);
        booksTable.setSelectionBackground(new Color(59, 130, 246));

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.getViewport().setBackground(new Color(20, 30, 50));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom - Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 30, 50));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addButton = new JButton("+ Thêm sách");
        addButton.setBackground(new Color(34, 197, 94));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addBook());
        buttonPanel.add(addButton);

        editButton = new JButton("✎ Chỉnh sửa");
        editButton.setBackground(new Color(59, 130, 246));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> editBook());
        buttonPanel.add(editButton);

        viewDetailsButton = new JButton("👁 Xem chi tiết");
        viewDetailsButton.setBackground(new Color(168, 85, 247));
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.addActionListener(e -> viewDetails());
        buttonPanel.add(viewDetailsButton);

        deleteButton = new JButton("🗑 Xóa");
        deleteButton.setBackground(new Color(239, 68, 68));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteBook());
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    void loadBooks() {
        tableModel.setRowCount(0);
        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                Category cat = categoryDAO.getCategoryById(product.getCategoryId());
                double avgRating = reviewDAO.getAverageRating(product.getId());
                String status = product.getStock() > 0 ? "Có sẵn" : "Hết hàng";
                
                tableModel.addRow(new Object[]{
                        product.getId(),
                        product.getName(),
                        product.getMaterial() != null ? product.getMaterial() : "N/A",
                        cat != null ? cat.getName() : "N/A",
                        product.getPrice(),
                        product.getStock(),
                        String.format("%.1f/5", avgRating),
                        status
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        Category allCategory = new Category();
        allCategory.setId(0L);
        allCategory.setName("Tất cả");
        categoryCombo.addItem(allCategory);
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            for (Category cat : categories) {
                categoryCombo.addItem(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void search() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Product> products = productDAO.searchProducts(keyword);
            for (Product product : products) {
                Category cat = categoryDAO.getCategoryById(product.getCategoryId());
                double avgRating = reviewDAO.getAverageRating(product.getId());
                String status = product.getStock() > 0 ? "Có sẵn" : "Hết hàng";
                
                tableModel.addRow(new Object[]{
                        product.getId(),
                        product.getName(),
                        product.getMaterial() != null ? product.getMaterial() : "N/A",
                        cat != null ? cat.getName() : "N/A",
                        product.getPrice(),
                        product.getStock(),
                        String.format("%.1f/5", avgRating),
                        status
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBook() {
        ProductEditDialog dialog = new ProductEditDialog(SwingUtilities.getWindowAncestor(this), null, categoryDAO);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadBooks();
        }
    }

    private void editBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long productId = (Long) tableModel.getValueAt(selectedRow, 0);
        try {
            Product product = productDAO.getProductById(productId);
            ProductEditDialog dialog = new ProductEditDialog(SwingUtilities.getWindowAncestor(this), product, categoryDAO);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadBooks();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sách này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long productId = (Long) tableModel.getValueAt(selectedRow, 0);
            try {
                if (productDAO.deleteProduct(productId)) {
                    JOptionPane.showMessageDialog(this, "Xóa sách thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa sách thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewDetails() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xem!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long productId = (Long) tableModel.getValueAt(selectedRow, 0);
        try {
            Product product = productDAO.getProductById(productId);
            List<Review> reviews = reviewDAO.getReviewsByProductId(productId);
            ProductDetailsDialog dialog = new ProductDetailsDialog(SwingUtilities.getWindowAncestor(this), product, reviews, categoryDAO, reviewDAO, currentUser);
            dialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
