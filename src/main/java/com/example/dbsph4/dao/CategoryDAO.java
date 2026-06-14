package com.example.dbsph4.dao;

import com.example.dbsph4.database.DatabaseConnection;
import com.example.dbsph4.models.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CategoryDAO {

    /**
     * QUERY 6: SELECT * FROM Categories
     * Fetches all registered tech categories from the database.
     */
    public static ObservableList<Category> getAllCategories() {
        ObservableList<Category> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Categories";
        System.out.println("[SQL-DAO] Executing: " + query);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] Failed to fetch categories.");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * QUERY 7: INSERT INTO Categories (PreparedStatement)
     * Inserts a new structural product category into the system inventory.
     */
    public static boolean insertCategory(Category category) {
        String query = "INSERT INTO Categories (CategoryName) VALUES (?)";
        System.out.println("[SQL-DAO] Executing Insert for Category: " + category.getCategoryName());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, category.getCategoryName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] Category insertion crashed.");
            e.printStackTrace();
            return false;
        }
    }
}