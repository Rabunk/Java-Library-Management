package com.library.gui;

import com.library.dao.UserDAO;
import com.library.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserPanel extends JPanel {
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public UserPanel() {
        this.userDAO = new UserDAO();
        initComponents();
        loadUsers();
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

        addButton = new JButton("+ Thêm độc giả");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addUser());
        topPanel.add(addButton);

        add(topPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(new Color(15, 23, 42));

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Tên", "Email", "Role", "Điện Thoại", "Địa Chỉ"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.setBackground(new Color(20, 30, 50));
        usersTable.setForeground(Color.WHITE);
        usersTable.setSelectionBackground(new Color(70, 130, 180));
        usersTable.getTableHeader().setBackground(new Color(30, 40, 60));
        usersTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.getViewport().setBackground(new Color(20, 30, 50));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel - Actions
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(20, 30, 50));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        editButton = new JButton("✎ Chỉnh sửa");
        editButton.setBackground(new Color(255, 152, 0));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> editUser());
        bottomPanel.add(editButton);

        deleteButton = new JButton("🗑 Xóa");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteUser());
        bottomPanel.add(deleteButton);

        tablePanel.add(bottomPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    void loadUsers() {
        try {
            tableModel.setRowCount(0);
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                tableModel.addRow(new Object[]{
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getPhone() != null ? user.getPhone() : "",
                        user.getAddress() != null ? user.getAddress() : ""
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void search() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadUsers();
            return;
        }

        try {
            tableModel.setRowCount(0);
            List<User> users = userDAO.getAllUsers();
            keyword = keyword.toLowerCase();
            for (User user : users) {
                if (user.getName().toLowerCase().contains(keyword) ||
                        user.getEmail().toLowerCase().contains(keyword) ||
                        (user.getPhone() != null && user.getPhone().contains(keyword))) {
                    tableModel.addRow(new Object[]{
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            user.getPhone() != null ? user.getPhone() : "",
                            user.getAddress() != null ? user.getAddress() : ""
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addUser() {
        UserEditDialog dialog = new UserEditDialog((Window) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadUsers();
        }
    }

    private void editUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả để chỉnh sửa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
            User user = userDAO.getUserById(userId);
            UserEditDialog dialog = new UserEditDialog((Window) SwingUtilities.getWindowAncestor(this), user);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadUsers();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả để xóa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa độc giả này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
                if (userDAO.deleteUser(userId)) {
                    JOptionPane.showMessageDialog(this, "Xóa độc giả thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa độc giả thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
