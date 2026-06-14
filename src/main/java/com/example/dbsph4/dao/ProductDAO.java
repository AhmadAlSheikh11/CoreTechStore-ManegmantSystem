package com.example.dbsph4.dao;

import com.example.dbsph4.database.DatabaseConnection;
import com.example.dbsph4.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ProductDAO {

    /**
     * BASE QUERY: SELECT * FROM Products
     * Used for loading initial catalog data safely.
     */
    public static ObservableList<Product> getAllProducts() {
        ObservableList<Product> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("ProductID"), rs.getString("ProductName"),
                        rs.getString("Description"), rs.getDouble("UnitPrice"),
                        rs.getDouble("Discount"), rs.getString("ImageURL"), rs.getInt("CategoryID")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 1: Retrieve a customer's shipping address and phone number by searching for their full name.
     */
    public static String getCustomerContactByName(String firstName, String lastName) {
        String query = "SELECT Phone, Address FROM Customers WHERE FirstName = ? AND LastName = ?";
        System.out.println("[SQL-DAO] Executing Query 1 for: " + firstName + " " + lastName);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "📞 Phone: " + rs.getString("Phone") + " | 📍 Address: " + rs.getString("Address");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "⚠️ Customer not found.";
    }

    /**
     * QUERY 14: Find all products with a UnitPrice between $100 and $500 within a specific category.
     */
    public static ObservableList<Product> getProductsByPriceRange(int categoryId, double minPrice, double maxPrice) {
        ObservableList<Product> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Products WHERE CategoryID = ? AND UnitPrice BETWEEN ? AND ?";
        System.out.println("[SQL-DAO] Executing Query 14 for Category ID: " + categoryId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, categoryId);
            pstmt.setDouble(2, minPrice);
            pstmt.setDouble(3, maxPrice);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getInt("ProductID"), rs.getString("ProductName"),
                            rs.getString("Description"), rs.getDouble("UnitPrice"),
                            rs.getDouble("Discount"), rs.getString("ImageURL"), rs.getInt("CategoryID")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 16: Search for all products that contain a specific word (e.g., "Gaming" or "Pro") in their name.
     */
    public static ObservableList<Product> searchProductsByName(String searchToken) {
        ObservableList<Product> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Products WHERE ProductName LIKE ?";
        System.out.println("[SQL-DAO] Executing Query 16 for keyword: " + searchToken);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + searchToken + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getInt("ProductID"), rs.getString("ProductName"),
                            rs.getString("Description"), rs.getDouble("UnitPrice"),
                            rs.getDouble("Discount"), rs.getString("ImageURL"), rs.getInt("CategoryID")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // CRUD Base Functions
    public static boolean insertProduct(Product p) {
        String query = "INSERT INTO Products (ProductName, Description, UnitPrice, Discount, ImageURL, CategoryID) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getProductName()); pstmt.setString(2, p.getDescription());
            pstmt.setDouble(3, p.getUnitPrice()); pstmt.setDouble(4, p.getDiscount());
            pstmt.setString(5, p.getImageUrl()); pstmt.setInt(6, p.getCategoryId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean updateProduct(Product p) {
        String query = "UPDATE Products SET ProductName=?, Description=?, UnitPrice=?, Discount=?, ImageURL=?, CategoryID=? WHERE ProductID=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getProductName()); pstmt.setString(2, p.getDescription());
            pstmt.setDouble(3, p.getUnitPrice()); pstmt.setDouble(4, p.getDiscount());
            pstmt.setString(5, p.getImageUrl()); pstmt.setInt(6, p.getCategoryId()); pstmt.setInt(7, p.getProductId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean deleteProduct(int id) {
        String query = "DELETE FROM Products WHERE ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id); return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}