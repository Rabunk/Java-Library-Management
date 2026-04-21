package com.library.gui;

import com.library.dao.ProductDAO;
import com.library.dao.CategoryDAO;
import com.library.dao.ReviewDAO;
import com.library.model.Product;
import com.library.model.Category;
import com.library.model.Review;
import com.library.model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProductDetailsDialog extends JDialog {
    private Product product;
    private List<Review> reviews;
    private CategoryDAO categoryDAO;
    private ReviewDAO reviewDAO;
    private User currentUser;

    public ProductDetailsDialog(Window parent, Product product, List<Review> reviews, CategoryDAO categoryDAO, ReviewDAO reviewDAO, User currentUser) {
        super((JFrame) parent, "Chi tiết sách", true);
        this.product = product;
        this.reviews = reviews;
        this.categoryDAO = categoryDAO;
        this.reviewDAO = reviewDAO;
        this.currentUser = currentUser;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(getOwner());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(15, 23, 42));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top section - Product info
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(20, 30, 50));
        infoPanel.setLayout(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        addInfoRow(infoPanel, "ID:", String.valueOf(product.getId()));
        addInfoRow(infoPanel, "Tên sách:", product.getName());
        
        try {
            Category cat = categoryDAO.getCategoryById(product.getCategoryId());
            addInfoRow(infoPanel, "Thể loại:", cat != null ? cat.getName() : "N/A");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addInfoRow(infoPanel, "Tác giả:", product.getMaterial() != null ? product.getMaterial() : "N/A");
        addInfoRow(infoPanel, "Giá:", String.format("%.2f", product.getPrice()));
        addInfoRow(infoPanel, "Giá gốc:", product.getOriginalPrice() != null ? String.format("%.2f", product.getOriginalPrice()) : "N/A");
        addInfoRow(infoPanel, "Tồn kho:", String.valueOf(product.getStock()));
        
        try {
            double avgRating = reviewDAO.getAverageRating(product.getId());
            addInfoRow(infoPanel, "Đánh giá trung bình:", String.format("%.1f/5", avgRating));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Middle section - Description
        JPanel descPanel = new JPanel();
        descPanel.setBackground(new Color(20, 30, 50));
        descPanel.setLayout(new BorderLayout(10, 10));
        descPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)), "Mô tả"));

        JTextArea descArea = new JTextArea(product.getDescription() != null ? product.getDescription() : "Không có mô tả");
        descArea.setEditable(false);
        descArea.setBackground(new Color(30, 40, 60));
        descArea.setForeground(Color.WHITE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descPanel.add(descScroll, BorderLayout.CENTER);

        mainPanel.add(descPanel, BorderLayout.CENTER);

        // Bottom section - Reviews
        JPanel reviewPanel = new JPanel();
        reviewPanel.setBackground(new Color(20, 30, 50));
        reviewPanel.setLayout(new BorderLayout(10, 10));
        reviewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)), "Đánh giá"));

        StringBuilder reviewText = new StringBuilder();
        if (reviews != null && !reviews.isEmpty()) {
            for (Review review : reviews) {
                reviewText.append("★ ").append(review.getRating()).append("/5\n");
                reviewText.append(review.getComment()).append("\n");
                reviewText.append("---\n");
            }
        } else {
            reviewText.append("Chưa có đánh giá nào");
        }

        JTextArea reviewArea = new JTextArea(reviewText.toString());
        reviewArea.setEditable(false);
        reviewArea.setBackground(new Color(30, 40, 60));
        reviewArea.setForeground(Color.WHITE);
        JScrollPane reviewScroll = new JScrollPane(reviewArea);
        reviewPanel.add(reviewScroll, BorderLayout.CENTER);

        JButton addReviewButton = new JButton("Thêm đánh giá");
        addReviewButton.setBackground(new Color(59, 130, 246));
        addReviewButton.setForeground(Color.WHITE);
        addReviewButton.setFocusPainted(false);
        addReviewButton.addActionListener(e -> addReview());
        reviewPanel.add(addReviewButton, BorderLayout.SOUTH);

        mainPanel.add(reviewPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(Color.WHITE);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(labelComp);

        JLabel valueComp = new JLabel(value);
        valueComp.setForeground(new Color(150, 200, 255));
        panel.add(valueComp);
    }

    private void addReview() {
        ReviewEditDialog dialog = new ReviewEditDialog(this, product.getId(), currentUser, reviewDAO);
        dialog.setVisible(true);
    }
}
