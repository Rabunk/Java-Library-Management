package com.library.gui;

import com.library.dao.ReviewDAO;
import com.library.model.Review;
import com.library.model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ReviewEditDialog extends JDialog {
    private Long productId;
    private User currentUser;
    private ReviewDAO reviewDAO;
    private JSpinner ratingSpinner;
    private JTextArea commentArea;

    public ReviewEditDialog(Window parent, Long productId, User currentUser, ReviewDAO reviewDAO) {
        super(parent, "Thêm đánh giá", ModalityType.APPLICATION_MODAL);
        this.productId = productId;
        this.currentUser = currentUser;
        this.reviewDAO = reviewDAO;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(getOwner());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(15, 23, 42));
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Rating
        JLabel ratingLabel = new JLabel("Đánh giá (1-5):");
        ratingLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(ratingLabel, gbc);

        ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        gbc.gridx = 1;
        mainPanel.add(ratingSpinner, gbc);

        // Comment
        JLabel commentLabel = new JLabel("Bình luận:");
        commentLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(commentLabel, gbc);

        commentArea = new JTextArea(5, 25);
        commentArea.setBackground(new Color(30, 40, 60));
        commentArea.setForeground(Color.WHITE);
        commentArea.setCaretColor(Color.WHITE);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(15, 23, 42));
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));

        JButton submitButton = new JButton("Gửi");
        submitButton.setBackground(new Color(34, 197, 94));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> submit());
        buttonPanel.add(submitButton);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(239, 68, 68));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void submit() {
        String comment = commentArea.getText().trim();
        Integer rating = (Integer) ratingSpinner.getValue();
        
        if (rating < 1 || rating > 5) {
            JOptionPane.showMessageDialog(this, "Đánh giá phải từ 1 đến 5", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (currentUser == null || currentUser.getId() == null) {
                JOptionPane.showMessageDialog(this, "Lỗi: Không xác định được người dùng", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (productId == null) {
                JOptionPane.showMessageDialog(this, "Lỗi: Không xác định được sản phẩm", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Review review = new Review(
                    currentUser.getId(),
                    productId,
                    rating,
                    comment.isEmpty() ? "" : comment
            );
            review.setCreatedAt(java.time.LocalDateTime.now());
            
            if (reviewDAO.addReview(review)) {
                JOptionPane.showMessageDialog(this, "Gửi đánh giá thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gửi đánh giá thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
