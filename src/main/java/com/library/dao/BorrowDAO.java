package com.library.dao;

import com.library.model.Borrow;
import com.library.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class BorrowDAO {
    
    public Borrow getBorrowById(Long id) throws SQLException {
        String sql = "SELECT * FROM borrows WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBorrow(rs);
                }
            }
        }
        return null;
    }

    public boolean addBorrow(Borrow borrow) throws SQLException {
        String sql = "INSERT INTO borrows (user_id, product_id, borrow_date, due_date, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, borrow.getUserId());
            pstmt.setLong(2, borrow.getProductId());
            pstmt.setTimestamp(3, Timestamp.valueOf(borrow.getBorrowDate()));
            pstmt.setTimestamp(4, Timestamp.valueOf(borrow.getDueDate()));
            pstmt.setString(5, "borrowing");
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Borrow> getAllBorrows() throws SQLException {
        List<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM borrows ORDER BY borrow_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                borrows.add(mapResultSetToBorrow(rs));
            }
        }
        return borrows;
    }

    public List<Borrow> getBorrowsByUserId(Long userId) throws SQLException {
        List<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM borrows WHERE user_id = ? ORDER BY borrow_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    borrows.add(mapResultSetToBorrow(rs));
                }
            }
        }
        return borrows;
    }

    public List<Borrow> getBorrowsByStatus(String status) throws SQLException {
        List<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM borrows WHERE status = ? ORDER BY borrow_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    borrows.add(mapResultSetToBorrow(rs));
                }
            }
        }
        return borrows;
    }

    public boolean returnBorrow(Long id, LocalDateTime returnDate) throws SQLException {
        String sql = "UPDATE borrows SET return_date = ?, status = 'returned', updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(returnDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(3, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateBorrowStatus(Long id, String status) throws SQLException {
        String sql = "UPDATE borrows SET status = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(3, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean addFine(Long id, BigDecimal fineAmount, String fineReason) throws SQLException {
        String sql = "UPDATE borrows SET fine_amount = ?, fine_reason = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, fineAmount);
            pstmt.setString(2, fineReason);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(4, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateBorrow(Borrow borrow) throws SQLException {
        String sql = "UPDATE borrows SET user_id = ?, product_id = ?, borrow_date = ?, due_date = ?, " +
                "return_date = ?, status = ?, fine_amount = ?, fine_reason = ?, notes = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, borrow.getUserId());
            pstmt.setLong(2, borrow.getProductId());
            pstmt.setTimestamp(3, Timestamp.valueOf(borrow.getBorrowDate()));
            pstmt.setTimestamp(4, Timestamp.valueOf(borrow.getDueDate()));
            pstmt.setTimestamp(5, borrow.getReturnDate() != null ? Timestamp.valueOf(borrow.getReturnDate()) : null);
            pstmt.setString(6, borrow.getStatus());
            pstmt.setBigDecimal(7, borrow.getFineAmount());
            pstmt.setString(8, borrow.getFineReason());
            pstmt.setString(9, borrow.getNotes());
            pstmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setLong(11, borrow.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteBorrow(Long id) throws SQLException {
        String sql = "DELETE FROM borrows WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Borrow mapResultSetToBorrow(ResultSet rs) throws SQLException {
        Borrow borrow = new Borrow();
        borrow.setId(rs.getLong("id"));
        borrow.setUserId(rs.getLong("user_id"));
        borrow.setProductId(rs.getLong("product_id"));
        borrow.setBorrowDate(rs.getTimestamp("borrow_date") != null ?
                rs.getTimestamp("borrow_date").toLocalDateTime() : null);
        borrow.setDueDate(rs.getTimestamp("due_date") != null ?
                rs.getTimestamp("due_date").toLocalDateTime() : null);
        borrow.setReturnDate(rs.getTimestamp("return_date") != null ?
                rs.getTimestamp("return_date").toLocalDateTime() : null);
        borrow.setStatus(rs.getString("status"));
        borrow.setFineAmount(rs.getBigDecimal("fine_amount"));
        borrow.setFineReason(rs.getString("fine_reason"));
        borrow.setNotes(rs.getString("notes"));
        borrow.setCreatedAt(rs.getTimestamp("created_at") != null ?
                rs.getTimestamp("created_at").toLocalDateTime() : null);
        borrow.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
                rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return borrow;
    }
}
