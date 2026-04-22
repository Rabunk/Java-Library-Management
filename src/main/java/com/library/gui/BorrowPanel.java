package com.library.gui;

import com.library.dao.BorrowDAO;
import com.library.dao.UserDAO;
import com.library.dao.ProductDAO;
import com.library.model.Borrow;
import com.library.model.User;
import com.library.model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class BorrowPanel extends JPanel {
    private JTable borrowsTable;
    private DefaultTableModel tableModel;
    private BorrowDAO borrowDAO;
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton returnButton;
    private JButton fineButton;
    private JButton deleteButton;

    public BorrowPanel() {
        this.borrowDAO = new BorrowDAO();
        this.userDAO = new UserDAO();
        this.productDAO = new ProductDAO();
        initComponents();
        loadBorrows();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 23, 42));

        // Top panel - Search and Filter
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(20, 30, 50));
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setForeground(Color.WHITE);
        topPanel.add(searchLabel);

        searchField = new JTextField(20);
        searchField.setBackground(new Color(30, 40, 60));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        topPanel.add(searchField);

        searchButton = new JButton("Tìm kiếm");
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> search());
        topPanel.add(searchButton);

        addButton = new JButton("+ Thêm mượn sách");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addBorrow());
        topPanel.add(addButton);

        add(topPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(new Color(15, 23, 42));

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Độc Giả", "Sách", "Ngày Mượn", "Hạn Trả", "Ngày Trả", "Trạng Thái", "Phí Phạt"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        borrowsTable = new JTable(tableModel);
        borrowsTable.setBackground(new Color(20, 30, 50));
        borrowsTable.setForeground(Color.WHITE);
        borrowsTable.setSelectionBackground(new Color(70, 130, 180));
        borrowsTable.getTableHeader().setBackground(new Color(30, 40, 60));
        borrowsTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(borrowsTable);
        scrollPane.getViewport().setBackground(new Color(20, 30, 50));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel - Actions
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(20, 30, 50));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        returnButton = new JButton("✓ Trả sách");
        returnButton.setBackground(new Color(76, 175, 80));
        returnButton.setForeground(Color.WHITE);
        returnButton.setFocusPainted(false);
        returnButton.addActionListener(e -> returnBook());
        bottomPanel.add(returnButton);

        fineButton = new JButton("💰 Tính phí phạt");
        fineButton.setBackground(new Color(255, 152, 0));
        fineButton.setForeground(Color.WHITE);
        fineButton.setFocusPainted(false);
        fineButton.addActionListener(e -> calculateFine());
        bottomPanel.add(fineButton);

        deleteButton = new JButton("🗑 Xóa");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteBorrow());
        bottomPanel.add(deleteButton);

        tablePanel.add(bottomPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    void loadBorrows() {
        try {
            tableModel.setRowCount(0);
            List<Borrow> borrows = borrowDAO.getAllBorrows();
            
            LocalDateTime now = LocalDateTime.now();
            
            for (Borrow borrow : borrows) {
                // Update status based on due date
                if (borrow.getReturnDate() == null && borrow.getDueDate().isBefore(now)) {
                    borrow.setStatus("overdue");
                    borrowDAO.updateBorrowStatus(borrow.getId(), "overdue");
                }
                
                try {
                    User user = userDAO.getUserById(borrow.getUserId());
                    Product product = productDAO.getProductById(borrow.getProductId());
                    
                    String userName = user != null ? user.getName() : "N/A";
                    String productName = product != null ? product.getName() : "N/A";
                    
                    tableModel.addRow(new Object[]{
                            borrow.getId(),
                            userName,
                            productName,
                            borrow.getBorrowDate(),
                            borrow.getDueDate(),
                            borrow.getReturnDate() != null ? borrow.getReturnDate() : "-",
                            borrow.getStatus(),
                            borrow.getFineAmount() != null ? String.format("%.2f VND", borrow.getFineAmount()) : "-"
                    });
                } catch (SQLException e) {
                    tableModel.addRow(new Object[]{
                            borrow.getId(),
                            "ERROR",
                            "ERROR",
                            borrow.getBorrowDate(),
                            borrow.getDueDate(),
                            borrow.getReturnDate() != null ? borrow.getReturnDate() : "-",
                            borrow.getStatus(),
                            borrow.getFineAmount() != null ? String.format("%.2f VND", borrow.getFineAmount()) : "-"
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void search() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadBorrows();
            return;
        }

        try {
            tableModel.setRowCount(0);
            List<Borrow> borrows = borrowDAO.getAllBorrows();
            keyword = keyword.toLowerCase();
            
            for (Borrow borrow : borrows) {
                try {
                    User user = userDAO.getUserById(borrow.getUserId());
                    Product product = productDAO.getProductById(borrow.getProductId());
                    
                    String userName = user != null ? user.getName() : "";
                    String productName = product != null ? product.getName() : "";
                    
                    if (userName.toLowerCase().contains(keyword) ||
                            productName.toLowerCase().contains(keyword) ||
                            String.valueOf(borrow.getId()).contains(keyword)) {
                        
                        tableModel.addRow(new Object[]{
                                borrow.getId(),
                                userName,
                                productName,
                                borrow.getBorrowDate(),
                                borrow.getDueDate(),
                                borrow.getReturnDate() != null ? borrow.getReturnDate() : "-",
                                borrow.getStatus(),
                                borrow.getFineAmount() != null ? String.format("%.2f VND", borrow.getFineAmount()) : "-"
                        });
                    }
                } catch (SQLException e) {
                    // Skip this entry
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBorrow() {
        BorrowEditDialog dialog = new BorrowEditDialog((Window) SwingUtilities.getWindowAncestor(this), null, userDAO, productDAO);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadBorrows();
        }
    }

    private void returnBook() {
        int selectedRow = borrowsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bản ghi để trả sách", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long borrowId = (Long) tableModel.getValueAt(selectedRow, 0);
            Borrow borrow = borrowDAO.getBorrowById(borrowId);
            
            if (borrow != null && "returned".equals(borrow.getStatus())) {
                JOptionPane.showMessageDialog(this, "Sách này đã được trả rồi", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            LocalDateTime returnDate = LocalDateTime.now();
            if (borrowDAO.returnBorrow(borrowId, returnDate)) {
                JOptionPane.showMessageDialog(this, "Trả sách thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadBorrows();
            } else {
                JOptionPane.showMessageDialog(this, "Trả sách thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateFine() {
        int selectedRow = borrowsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bản ghi để tính phí phạt", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long borrowId = (Long) tableModel.getValueAt(selectedRow, 0);
            Borrow borrow = borrowDAO.getBorrowById(borrowId);
            
            if (borrow == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            FineDialog dialog = new FineDialog((Window) SwingUtilities.getWindowAncestor(this), borrow, borrowDAO);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadBorrows();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBorrow() {
        int selectedRow = borrowsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bản ghi để xóa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa bản ghi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long borrowId = (Long) tableModel.getValueAt(selectedRow, 0);
                if (borrowDAO.deleteBorrow(borrowId)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadBorrows();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
