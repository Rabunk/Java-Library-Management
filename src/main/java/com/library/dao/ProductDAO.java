package com.library.dao;

import com.library.model.Product;
import com.library.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    public Product getProductById(Long id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }

    public boolean addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, description, original_price, price, stock, material, category_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setBigDecimal(3, product.getOriginalPrice());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setInt(5, product.getStock());
            pstmt.setString(6, product.getMaterial());
            pstmt.setLong(7, product.getCategoryId());
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    public List<Product> getProductsByCategory(Long categoryId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }

    public List<Product> searchProducts(String keyword) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ? OR material LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            pstmt.setString(3, searchKeyword);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }

    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, description = ?, original_price = ?, price = ?, stock = ?, material = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setBigDecimal(3, product.getOriginalPrice());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setInt(5, product.getStock());
            pstmt.setString(6, product.getMaterial());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(8, product.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteProduct(Long id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setOriginalPrice(rs.getBigDecimal("original_price"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStock(rs.getInt("stock"));
        product.setMaterial(rs.getString("material"));
        product.setCategoryId(rs.getLong("category_id"));
        product.setCreatedAt(rs.getTimestamp("created_at") != null ? 
                rs.getTimestamp("created_at").toLocalDateTime() : null);
        product.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
                rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return product;
    }
}
