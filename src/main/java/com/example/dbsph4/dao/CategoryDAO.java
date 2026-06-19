package com.example.dbsph4.dao;

import com.example.dbsph4.database.DatabaseConnection;
import com.example.dbsph4.models.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class CategoryDAO {

    /**
     * BASE QUERY: Select all categories — SELECT from Categories table.
     */
    public static ObservableList<Category> getAllCategories() {
        ObservableList<Category> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Categories";
        System.out.println("[SQL-DAO] Fetching all categories.");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        rs.getInt("WarrantyMonths")
                ));
            }
            System.out.println("[SQL-DAO] Loaded " + list.size() + " category/categories.");
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getAllCategories failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * BASE QUERY: Insert a new product category — INSERT into Categories table.
     * New categories default to a 12-month warranty unless specified otherwise.
     */
    public static boolean insertCategory(Category category) {
        String query = "INSERT INTO Categories (CategoryName, WarrantyMonths) VALUES (?, ?)";
        System.out.println("[SQL-DAO] Inserting category: " + category.getCategoryName() +
                " (Warranty: " + category.getWarrantyMonths() + " months)");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, category.getCategoryName());
            pstmt.setInt(2, category.getWarrantyMonths());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SQL-DAO] Category inserted: " + category.getCategoryName());
                return true;
            }
            System.err.println("[SQL-DAO-ERROR] INSERT returned 0 rows for category: " + category.getCategoryName());
            return false;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] insertCategory failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * BASE QUERY: Update an existing category's name and/or warranty duration.
     * Lets an admin fix a typo'd category name or change its warranty policy
     * at any time — UPDATE Categories table.
     */
    public static boolean updateCategory(int categoryId, String newName, int newWarrantyMonths) {
        String query = "UPDATE Categories SET CategoryName = ?, WarrantyMonths = ? WHERE CategoryID = ?";
        System.out.println("[SQL-DAO] Updating Category ID " + categoryId +
                " -> name='" + newName + "', warranty=" + newWarrantyMonths + " months");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, newWarrantyMonths);
            pstmt.setInt(3, categoryId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SQL-DAO] Category updated: ID=" + categoryId);
                return true;
            }
            System.err.println("[SQL-DAO-ERROR] UPDATE returned 0 rows for Category ID: " + categoryId);
            return false;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] updateCategory failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Look up the warranty duration (in months) configured for the category
     * a given product belongs to. Falls back to 12 months if not found,
     * so checkout never fails even if a product's category lookup misses.
     */
    public static int getWarrantyMonthsForProduct(int productId) {
        String query = "SELECT c.WarrantyMonths FROM Products p " +
                "JOIN Categories c ON p.CategoryID = c.CategoryID " +
                "WHERE p.ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("WarrantyMonths");
            }
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getWarrantyMonthsForProduct failed: " + e.getMessage());
            e.printStackTrace();
        }
        System.err.println("[SQL-DAO-WARN] No category warranty found for Product ID " + productId + " — defaulting to 12 months.");
        return 12;
    }
}