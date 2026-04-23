package com.library.gui;

import com.library.model.User;
import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private LibraryPanel libraryPanel;
    private UserPanel userPanel;
    private BorrowPanel borrowPanel;

    public AdminPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));

        // Sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(10, 15, 30));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Logo/Title
        JLabel logoLabel = new JLabel("Library Admin");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logoLabel.setForeground(new Color(100, 150, 255));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 15));
        sidebarPanel.add(logoLabel);

        JLabel subtitleLabel = new JLabel("Admin Panel");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(150, 150, 150));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 30, 15));
        sidebarPanel.add(subtitleLabel);

        // Menu Items
        JButton libBtn = createMenuButton("Kho Sách", "library");
        libBtn.addActionListener(e -> showPanel("library"));
        sidebarPanel.add(libBtn);

        JButton userBtn = createMenuButton("Độc Giả", "user");
        userBtn.addActionListener(e -> showPanel("user"));
        sidebarPanel.add(userBtn);

        JButton borrowBtn = createMenuButton("Mượn / Trả", "borrow");
        borrowBtn.addActionListener(e -> showPanel("borrow"));
        sidebarPanel.add(borrowBtn);

        sidebarPanel.add(Box.createVerticalGlue());

        // Content Panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(15, 23, 42));

        // Initialize panels
        User adminUser = new User("Admin", "admin@library.com", "", "admin");
        adminUser.setId(1L);
        libraryPanel = new LibraryPanel(adminUser);
        userPanel = new UserPanel();
        borrowPanel = new BorrowPanel();

        contentPanel.add(libraryPanel, "library");
        contentPanel.add(userPanel, "user");
        contentPanel.add(borrowPanel, "borrow");

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Show library panel by default
        cardLayout.show(contentPanel, "library");
    }

    private JButton createMenuButton(String text, String actionCommand) {
        JButton button = new JButton(text);
        button.setActionCommand(actionCommand);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 40, 60));
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(200, 50));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 80, 120));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 40, 60));
            }
        });
        
        return button;
    }

    private void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    public void refreshPanels() {
        libraryPanel.loadBooks();
        userPanel.loadUsers();
        borrowPanel.loadBorrows();
    }
}
