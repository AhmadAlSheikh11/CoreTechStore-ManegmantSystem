package com.example.dbsph4.dao;

import com.example.dbsph4.database.DatabaseConnection;
import com.example.dbsph4.models.Warehouse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class WarehouseDAO {

    /**
     * BASE QUERY: Fetch all warehouses — SELECT from Warehouses table.
     */
    public static ObservableList<Warehouse> getAllWarehouses() {
        ObservableList<Warehouse> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Warehouses";
        System.out.println("[SQL-DAO] Fetching all warehouses.");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Warehouse(
                        rs.getInt("WarehouseID"),
                        rs.getString("WarehouseName"),
                        rs.getString("Location")
                ));
            }
            System.out.println("[SQL-DAO] Loaded " + list.size() + " warehouse(s).");
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getAllWarehouses failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * BASE QUERY: Insert a new warehouse — INSERT into Warehouses table.
     */
    public static boolean insertWarehouse(String name, String location) {
        String query = "INSERT INTO Warehouses (WarehouseName, Location) VALUES (?, ?)";
        System.out.println("[SQL-DAO] Inserting warehouse: " + name + " @ " + location);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, location);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SQL-DAO] Warehouse inserted: " + name);
                return true;
            }
            System.err.println("[SQL-DAO-ERROR] INSERT returned 0 rows for warehouse: " + name);
            return false;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] insertWarehouse failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * BASE QUERY: Get live stock levels for a warehouse — SELECT from Stored_in.
     */
    public static ObservableList<String[]> getStockLevels(int warehouseId) {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductName, s.StockQuantity, p.ProductID " +
                "FROM Stored_in s JOIN Products p ON s.ProductID = p.ProductID " +
                "WHERE s.WarehouseID = ?";
        System.out.println("[SQL-DAO] Fetching stock for warehouse ID: " + warehouseId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, warehouseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("ProductName"),
                            String.valueOf(rs.getInt("StockQuantity")),
                            String.valueOf(rs.getInt("ProductID"))
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getStockLevels failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * BASE QUERY: Update stock quantity for a product in a warehouse — UPDATE Stored_in.
     */
    public static boolean updateStockQuantity(int warehouseId, int productId, int newQty) {
        String query = "UPDATE Stored_in SET StockQuantity = ? WHERE WarehouseID = ? AND ProductID = ?";
        System.out.println("[SQL-DAO] Updating stock: WH=" + warehouseId + " Prod=" + productId + " Qty=" + newQty);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, newQty);
            pstmt.setInt(2, warehouseId);
            pstmt.setInt(3, productId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SQL-DAO] Stock updated successfully.");
                return true;
            }
            System.err.println("[SQL-DAO-ERROR] UPDATE returned 0 rows — product/warehouse combination not found.");
            return false;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] updateStockQuantity failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getSpecificStockQuantity failed: " + e.getMessage());
            e.printStackTrace();
        }
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
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getOutOfStockProducts failed: " + e.getMessage());
            e.printStackTrace();
        }
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
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getCategoryProductCounts failed: " + e.getMessage());
            e.printStackTrace();
        }
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
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getLowestTenStocks failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}