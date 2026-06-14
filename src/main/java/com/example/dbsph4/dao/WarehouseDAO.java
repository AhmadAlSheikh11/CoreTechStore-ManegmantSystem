package com.example.dbsph4.dao;

import com.example.dbsph4.database.DatabaseConnection;
import com.example.dbsph4.models.Warehouse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class WarehouseDAO {

    public static ObservableList<Warehouse> getAllWarehouses() {
        ObservableList<Warehouse> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Warehouses";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Warehouse(rs.getInt("WarehouseID"), rs.getString("WarehouseName"), rs.getString("Location")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 12: Check the current stock quantity for a specific product at a specific warehouse location.
     */
    public static int getSpecificStockQuantity(int warehouseId, int productId) {
        String query = "SELECT StockQuantity FROM Stored_in WHERE WarehouseID = ? AND ProductID = ?";
        System.out.println("[SQL-DAO] Executing Query 12 for WH: " + warehouseId + ", Prod: " + productId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, warehouseId);
            pstmt.setInt(2, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("StockQuantity");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * QUERY 13: List all products that are currently out of stock (StockQuantity = 0) in any of the warehouses.
     */
    public static ObservableList<String[]> getOutOfStockProducts() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT w.WarehouseName, p.ProductName FROM Stored_in s " +
                "JOIN Products p ON s.ProductID = p.ProductID " +
                "JOIN Warehouses w ON s.WarehouseID = w.WarehouseID WHERE s.StockQuantity = 0";
        System.out.println("[SQL-DAO] Executing Query 13 (Out of Stock items)");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("WarehouseName"), rs.getString("ProductName")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 15: Get a count of the total number of products available in each Category (e.g., Laptops, Smartphones).
     */
    public static ObservableList<String[]> getCategoryProductCounts() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT c.CategoryName, COUNT(p.ProductID) as TotalProds FROM Categories c " +
                "LEFT JOIN Products p ON c.CategoryID = p.CategoryID GROUP BY c.CategoryID, c.CategoryName";
        System.out.println("[SQL-DAO] Executing Query 15 (Category product distribution)");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("CategoryName"), String.valueOf(rs.getInt("TotalProds"))});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 18: Find the 10 products with the lowest total stock quantity across all warehouses to prioritize restocking.
     */
    public static ObservableList<String[]> getLowestTenStocks() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductName, SUM(s.StockQuantity) as TotalStock FROM Stored_in s " +
                "JOIN Products p ON s.ProductID = p.ProductID " +
                "GROUP BY s.ProductID, p.ProductName ORDER BY TotalStock ASC LIMIT 10";
        System.out.println("[SQL-DAO] Executing Query 18 (Lowest 10 Stock Items)");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("ProductName"), String.valueOf(rs.getInt("TotalStock"))});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean insertWarehouse(String name, String location) {
        String query = "INSERT INTO Warehouses (WarehouseName, Location) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name); pstmt.setString(2, location);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static ObservableList<String[]> getStockLevels(int warehouseId) {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductName, s.StockQuantity, p.ProductID FROM Stored_in s JOIN Products p ON s.ProductID = p.ProductID WHERE s.WarehouseID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, warehouseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[] { rs.getString("ProductName"), String.valueOf(rs.getInt("StockQuantity")), String.valueOf(rs.getInt("ProductID")) });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean updateStockQuantity(int warehouseId, int productId, int newQty) {
        String query = "UPDATE Stored_in SET StockQuantity = ? WHERE WarehouseID = ? AND ProductID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, newQty); pstmt.setInt(2, warehouseId); pstmt.setInt(3, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}