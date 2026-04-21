package com.library.dao;

import com.library.model.Category;
import com.library.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    
    public Category getCategoryById(Long id) throws SQLException {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        }
        return null;
    }

    public boolean addCategory(Category category) throws SQLException {
        String sql = "INSERT INTO categories (name, description, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        }
        return categories;
    }

    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ?, description = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(4, category.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCategory(Long id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setCreatedAt(rs.getTimestamp("created_at") != null ? 
                rs.getTimestamp("created_at").toLocalDateTime() : null);
        category.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
                rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return category;
    }
}
