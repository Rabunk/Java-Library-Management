package com.library.gui;

import com.library.dao.BorrowDAO;
import com.library.model.Borrow;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FineDialog extends JDialog {
    private Long borrowId;
    private Borrow borrow;
    private BorrowDAO borrowDAO;
    private JTextField fineAmountField;
    private JTextArea fineReasonArea;
    private JTextArea notesArea;
    private JButton submitButton;
    private JButton cancelButton;
    private boolean success = false;

    public FineDialog(Window parent, Borrow borrow, BorrowDAO borrowDAO) {
        super((JFrame) parent, "Tính phí phạt", true);
        this.borrowId = borrow.getId();
        this.borrow = borrow;
        this.borrowDAO = borrowDAO;
        initComponents();
        calculateDefaultFine();
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 450);
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
        JLabel titleLabel = new JLabel("Tính Phí Phạt Trễ Hạn");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Display borrow info
        JLabel infoLabel = new JLabel(String.format("Sách ID: %d | Hạn trả: %s", 
                borrow.getProductId(), borrow.getDueDate()));
        infoLabel.setForeground(new Color(150, 200, 255));
        gbc.gridy = 1;
        mainPanel.add(infoLabel, gbc);

        // Fine Amount
        JLabel fineAmountLabel = new JLabel("Phí phạt (VND):");
        fineAmountLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(fineAmountLabel, gbc);

        fineAmountField = new JTextField(20);
        fineAmountField.setBackground(new Color(30, 40, 60));
        fineAmountField.setForeground(Color.WHITE);
        fineAmountField.setCaretColor(Color.WHITE);
        fineAmountField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120)));
        gbc.gridx = 1;
        mainPanel.add(fineAmountField, gbc);

        // Fine Reason
        JLabel fineReasonLabel = new JLabel("Nguyên do phạt:");
        fineReasonLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(fineReasonLabel, gbc);

        fineReasonArea = new JTextArea(3, 20);
        fineReasonArea.setBackground(new Color(30, 40, 60));
        fineReasonArea.setForeground(Color.WHITE);
        fineReasonArea.setCaretColor(Color.WHITE);
        fineReasonArea.setLineWrap(true);
        fineReasonArea.setWrapStyleWord(true);
        JScrollPane reasonScrollPane = new JScrollPane(fineReasonArea);
        gbc.gridx = 1;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(reasonScrollPane, gbc);

        // Notes
        JLabel notesLabel = new JLabel("Ghi chú:");
        notesLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(notesLabel, gbc);

        notesArea = new JTextArea(3, 20);
        notesArea.setBackground(new Color(30, 40, 60));
        notesArea.setForeground(Color.WHITE);
        notesArea.setCaretColor(Color.WHITE);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        gbc.gridx = 1;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(notesScrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(15, 23, 42));
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));

        submitButton = new JButton("Xác nhận");
        submitButton.setBackground(new Color(76, 175, 80));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> submit());
        buttonPanel.add(submitButton);

        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
    }

    private void calculateDefaultFine() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = borrow.getDueDate();
        
        if (dueDate.isBefore(now)) {
            // Calculate days overdue
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, now);
            if (daysOverdue < 0) daysOverdue = 0;
            
            // 5000 VND per day
            double fineAmount = daysOverdue * 5000;
            fineAmountField.setText(String.format("%.0f", fineAmount));
            fineReasonArea.setText("Trễ hạn " + daysOverdue + " ngày");
        } else {
            fineAmountField.setText("0");
            fineReasonArea.setText("");
        }
    }

    private void submit() {
        String fineAmountStr = fineAmountField.getText().trim();
        String fineReason = fineReasonArea.getText().trim();
        String notes = notesArea.getText().trim();

        if (fineAmountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập phí phạt", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal fineAmount = new BigDecimal(fineAmountStr);
            
            // Update borrow with fine info
            if (borrowDAO.addFine(borrowId, fineAmount, fineReason)) {
                if (!notes.isEmpty()) {
                    borrow.setNotes(notes);
                    borrowDAO.updateBorrow(borrow);
                }
                
                JOptionPane.showMessageDialog(this, "Tính phí phạt thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                success = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật phí phạt thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Phí phạt phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
