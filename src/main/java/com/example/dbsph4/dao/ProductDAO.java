package com.example.dbsph4.dao;

import com.example.dbsph4.database.DatabaseConnection;
import com.example.dbsph4.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class ProductDAO {

    /**
     * BASE QUERY: Load all products — SELECT from Products table.
     */
    public static ObservableList<Product> getAllProducts() {
        ObservableList<Product> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Products";
        System.out.println("[SQL-DAO] Fetching all products.");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            System.out.println("[SQL-DAO] Loaded " + list.size() + " product(s).");
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getAllProducts failed: " + e.getMessage());
            e.printStackTrace();
        }
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
                    return "Phone: " + rs.getString("Phone") + " | Address: " + rs.getString("Address");
                }
            }
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getCustomerContactByName failed: " + e.getMessage());
            e.printStackTrace();
        }
        return "Customer not found.";
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
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getProductsByPriceRange failed: " + e.getMessage());
            e.printStackTrace();
        }
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
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] searchProductsByName failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * BASE QUERY: Get all products within a specific category — SELECT from Products.
     */
    public static ObservableList<Product> getProductsByCategory(int categoryId) {
        ObservableList<Product> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Products WHERE CategoryID = ?";
        System.out.println("[SQL-DAO] Fetching products for Category ID: " + categoryId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getProductsByCategory failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * BASE QUERY: Insert a single product — INSERT into Products table.
     */
    public static boolean insertProduct(Product p) {
        String query = "INSERT INTO Products (ProductName, Description, UnitPrice, Discount, ImageURL, CategoryID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        System.out.println("[SQL-DAO] Inserting product: " + p.getProductName());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getProductName());
            pstmt.setString(2, p.getDescription());
            pstmt.setDouble(3, p.getUnitPrice());
            pstmt.setDouble(4, p.getDiscount());
            pstmt.setString(5, p.getImageUrl());
            pstmt.setInt(6, p.getCategoryId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SQL-DAO] Product inserted: " + p.getProductName());
                return true;
            }
            System.err.println("[SQL-DAO-ERROR] INSERT returned 0 rows for product: " + p.getProductName());
            return false;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] insertProduct failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * BASE QUERY: Update an existing product — UPDATE Products table.
     */
    public static boolean updateProduct(Product p) {
        String query = "UPDATE Products SET ProductName=?, Description=?, UnitPrice=?, Discount=?, ImageURL=?, CategoryID=? " +
                "WHERE ProductID=?";
        System.out.println("[SQL-DAO] Updating product ID: " + p.getProductId());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getProductName());
            pstmt.setString(2, p.getDescription());
            pstmt.setDouble(3, p.getUnitPrice());
            pstmt.setDouble(4, p.getDiscount());
            pstmt.setString(5, p.getImageUrl());
            pstmt.setInt(6, p.getCategoryId());
            pstmt.setInt(7, p.getProductId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SQL-DAO] Product updated: " + p.getProductName());
                return true;
            }
            System.err.println("[SQL-DAO-ERROR] UPDATE returned 0 rows for product ID: " + p.getProductId());
            return false;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] updateProduct failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * BASE QUERY: Delete a product by ID — DELETE from Products table.
     */
    public static boolean deleteProduct(int id) {
        String query = "DELETE FROM Products WHERE ProductID = ?";
        System.out.println("[SQL-DAO] Deleting product ID: " + id);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SQL-DAO] Product deleted: ID=" + id);
                return true;
            }
            System.err.println("[SQL-DAO-ERROR] DELETE returned 0 rows for product ID: " + id);
            return false;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] deleteProduct failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * TRANSACTIONAL INSERT: Insert product into Products and create Stored_in
     * records for every warehouse (initial stock for selected WH, 0 for others).
     * Uses a manual transaction — commits only if all inserts succeed.
     */
    public static boolean insertProduct(Product p, int warehouseId, int initialStock) {
        String productQuery = "INSERT INTO Products (ProductName, Description, UnitPrice, Discount, ImageURL, CategoryID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String stockQuery   = "INSERT INTO Stored_in (WarehouseID, ProductID, StockQuantity) VALUES (?, ?, ?)";
        String whQuery      = "SELECT WarehouseID FROM Warehouses WHERE WarehouseID <> ?";

        System.out.println("[SQL-DAO] Transactional insert: " + p.getProductName() + " → WH " + warehouseId);
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            // 1. Insert into Products and get generated ProductID
            int productId;
            try (PreparedStatement pstmt = conn.prepareStatement(productQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, p.getProductName());
                pstmt.setString(2, p.getDescription());
                pstmt.setDouble(3, p.getUnitPrice());
                pstmt.setDouble(4, p.getDiscount());
                pstmt.setString(5, p.getImageUrl());
                pstmt.setInt(6, p.getCategoryId());
                if (pstmt.executeUpdate() == 0) throw new SQLException("Product INSERT returned 0 rows.");
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No generated ProductID returned.");
                    productId = keys.getInt(1);
                }
            }

            // 2. Insert Stored_in for the selected warehouse with initial stock
            try (PreparedStatement stockPstmt = conn.prepareStatement(stockQuery)) {
                stockPstmt.setInt(1, warehouseId);
                stockPstmt.setInt(2, productId);
                stockPstmt.setInt(3, initialStock);
                stockPstmt.executeUpdate();
            }

            // 3. Insert Stored_in for every other warehouse with 0 stock
            try (PreparedStatement whPstmt = conn.prepareStatement(whQuery)) {
                whPstmt.setInt(1, warehouseId);
                try (ResultSet rs = whPstmt.executeQuery();
                     PreparedStatement stockOther = conn.prepareStatement(stockQuery)) {
                    while (rs.next()) {
                        stockOther.setInt(1, rs.getInt("WarehouseID"));
                        stockOther.setInt(2, productId);
                        stockOther.setInt(3, 0);
                        stockOther.addBatch();
                    }
                    stockOther.executeBatch();
                }
            }

            conn.commit();
            p.setProductId(productId);
            System.out.println("[SQL-DAO] Product + Stored_in committed. ProductID=" + productId);
            return true;

        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] Transactional product insert failed: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private static Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("ProductID"),
                rs.getString("ProductName"),
                rs.getString("Description"),
                rs.getDouble("UnitPrice"),
                rs.getDouble("Discount"),
                rs.getString("ImageURL"),
                rs.getInt("CategoryID")
        );
    }
}