package com.library.gui;

import com.library.model.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;
    private AdminPanel adminPanel;
    private CustomerPanel customerPanel;
    private JLabel userLabel;

    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(true);
        setTitle("Mini-Library - Quản lý Thư Viện");

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(20, 30, 50));
        menuBar.setForeground(Color.WHITE);

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // User menu
        JMenu userMenu = new JMenu("Người dùng");
        userMenu.setForeground(Color.WHITE);
        
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        logoutItem.addActionListener(e -> logout());
        userMenu.add(logoutItem);

        // Help menu
        JMenu helpMenu = new JMenu("Trợ giúp");
        helpMenu.setForeground(Color.WHITE);
        
        JMenuItem aboutItem = new JMenuItem("Về ứng dụng");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(userMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        // Top panel - User info
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(20, 30, 50));
        topPanel.setLayout(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        String titleText = "admin".equalsIgnoreCase(currentUser.getRole()) ? 
                "Admin Dashboard" : "Trang chủ khách hàng";
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        userLabel = new JLabel("👤 " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        userLabel.setForeground(new Color(150, 200, 255));
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(userLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Main content - show different panels based on role
        if ("admin".equalsIgnoreCase(currentUser.getRole())) {
            adminPanel = new AdminPanel();
            add(adminPanel, BorderLayout.CENTER);
        } else {
            customerPanel = new CustomerPanel(currentUser);
            add(customerPanel, BorderLayout.CENTER);
        }

        setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginDialog loginDialog = new LoginDialog(null);
                loginDialog.setVisible(true);

                if (loginDialog.isLoginSuccess()) {
                    User user = loginDialog.getLoggedInUser();
                    new MainFrame(user);
                } else {
                    System.exit(0);
                }
            });
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(
                this,
                "Mini-Library v1.0\n\n" +
                "Ứng dụng quản lý thư viện sách\n" +
                "Sử dụng: Java Swing + JDBC + MySQL\n\n" +
                "(c) Library Management System",
                "Về ứng dụng",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            if (loginDialog.isLoginSuccess()) {
                User user = loginDialog.getLoggedInUser();
                new MainFrame(user);
            } else {
                System.exit(0);
            }
        });
    }
}
