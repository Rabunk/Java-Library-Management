package com.library.gui;

import com.library.model.User;
import javax.swing.*;
import java.awt.*;

public class CustomerPanel extends JPanel {
    private User currentUser;

    public CustomerPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));

        // Welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(new Color(15, 23, 42));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));
        welcomePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("Chào mừng!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomePanel.add(titleLabel);

        // User name
        JLabel userLabel = new JLabel("Xin chào, " + (currentUser != null ? currentUser.getName() : "Bạn"));
        userLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        userLabel.setForeground(new Color(150, 200, 255));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(userLabel);

        welcomePanel.add(Box.createVerticalGlue());

        add(welcomePanel, BorderLayout.CENTER);
    }
}
