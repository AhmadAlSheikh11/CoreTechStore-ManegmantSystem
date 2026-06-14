package com.example.dbsph4.dao;

import com.example.dbsph4.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class TransactionDAO {

    /**
     * QUERY 2: Calculate the total quantity of items purchased by a specific customer across all their past transactions.
     */
    public static int getTotalItemsPurchasedByCustomer(int customerId) {
        String query = "SELECT SUM(Quantity) FROM Transaction_Items WHERE CustomerID = ?";
        System.out.println("[SQL-DAO] Executing Query 2 for Customer ID: " + customerId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * QUERY 3: Find the exact Price At Sale and Warranty End Date for a specific product purchased in a specific Transaction.
     */
    public static String getPriceAndWarrantyForProduct(int customerId, int transactionId, int productId) {
        String query = "SELECT ti.PriceAtSale, w.WarrantyEndDate FROM Transaction_Items ti " +
                "LEFT JOIN Warranties w ON ti.CustomerID = w.CustomerID AND ti.ProductID = w.ProductID " +
                "WHERE ti.CustomerID = ? AND ti.TransactionID = ? AND ti.ProductID = ?";
        System.out.println("[SQL-DAO] Executing Query 3 for Transaction Item validation");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, transactionId);
            pstmt.setInt(3, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "💰 Price at Sale: $" + rs.getDouble("PriceAtSale") + " | 🛡️ Warranty End: " + rs.getDate("WarrantyEndDate");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "No sales record found.";
    }

    /**
     * QUERY 4: List all products, their purchased quantities, and warranty expiration dates for the most recent transaction belonging to the email "Ahmad@gmail.com".
     */
    public static ObservableList<String[]> getAhmadMostRecentTransactionDetails() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductName, ti.Quantity, w.WarrantyEndDate FROM Transaction_Items ti " +
                "JOIN Products p ON ti.ProductID = p.ProductID " +
                "JOIN Customers c ON ti.CustomerID = c.CustomerID " +
                "LEFT JOIN Warranties w ON ti.CustomerID = w.CustomerID AND ti.ProductID = w.ProductID " +
                "WHERE c.Email = 'Ahmad@gmail.com' AND ti.TransactionID = (SELECT MAX(TransactionID) FROM Transactions WHERE CustomerID = c.CustomerID)";
        System.out.println("[SQL-DAO] Executing Query 4 for Ahmad@gmail.com");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("ProductName"),
                        String.valueOf(rs.getInt("Quantity")),
                        String.valueOf(rs.getDate("WarrantyEndDate"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 5: Calculate the total online sales revenue generated across the entire E-commerce platform for the current month.
     */
    public static double getCurrentMonthRevenue() {
        // Enforcing Year 2026 handling as per requirements context
        String query = "SELECT SUM(TotalAmount) FROM Transactions WHERE MONTH(OrderDate) = MONTH(CURRENT_DATE) AND YEAR(OrderDate) = 2026";
        System.out.println("[SQL-DAO] Executing Query 5 (Monthly Revenue Analysis)");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    /**
     * QUERY 6: Find the most recent online transaction (last purchase) made by a specific customer, including the timestamp.
     */
    public static String getMostRecentTransactionForCustomer(int customerId) {
        String query = "SELECT TransactionID, OrderDate, TotalAmount FROM Transactions WHERE CustomerID = ? ORDER BY TransactionID DESC LIMIT 1";
        System.out.println("[SQL-DAO] Executing Query 6 for customer tracking");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "Invoice ID: " + rs.getInt("TransactionID") + " | 📅 Date: " + rs.getTimestamp("OrderDate") + " | 💵 Paid: $" + rs.getDouble("TotalAmount");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "No transactions registered.";
    }

    /**
     * QUERY 7: List all transactions (TransactionID and Date) that included a specific electronic product.
     */
    public static ObservableList<String[]> getTransactionsByProduct(int productId) {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT t.CustomerID, t.TransactionID, t.OrderDate FROM Transaction_Items ti " +
                "JOIN Transactions t ON ti.CustomerID = t.CustomerID AND ti.TransactionID = t.TransactionID " +
                "WHERE ti.ProductID = ?";
        System.out.println("[SQL-DAO] Executing Query 7 for Product tracking ID: " + productId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{ "Cust ID: " + rs.getInt("CustomerID"), "Invoice: " + rs.getInt("TransactionID"), String.valueOf(rs.getDate("OrderDate")) });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 8: Find the top 3 best-selling products based on the total online revenue they generated.
     */
    public static ObservableList<String[]> getTopThreeRevenueProducts() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductName, SUM(ti.Quantity * ti.PriceAtSale) as GrossProfit FROM Transaction_Items ti " +
                "JOIN Products p ON ti.ProductID = p.ProductID " +
                "GROUP BY ti.ProductID, p.ProductName ORDER BY GrossProfit DESC LIMIT 3";
        System.out.println("[SQL-DAO] Executing Query 8 (Top 3 Products Revenue)");
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{ rs.getString("ProductName"), String.format("$%.2f", rs.getDouble("GrossProfit")) });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 9: Calculate the average number of items purchased per online transaction.
     */
    public static double getAverageItemsPerTransaction() {
        String query = "SELECT AVG(TotalItems) FROM (SELECT COUNT(ProductID) as TotalItems FROM Transaction_Items GROUP BY CustomerID, TransactionID) as OrderCounts";
        System.out.println("[SQL-DAO] Executing Query 9 (Average Items Calculation)");
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    /**
     * QUERY 10: Retrieve names, emails, and phone numbers of all customers whose product warranties are expiring within the next 30 days.
     */
    public static ObservableList<String[]> getWarrantiesExpiringIn30Days() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT c.FirstName, c.LastName, c.Email, c.Phone, w.WarrantyEndDate FROM Warranties w " +
                "JOIN Customers c ON w.CustomerID = c.CustomerID " +
                "WHERE w.WarrantyEndDate BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY)";
        System.out.println("[SQL-DAO] Executing Query 10 (Expiring Warranties 30 Days Alert)");
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{ rs.getString("FirstName") + " " + rs.getString("LastName"), rs.getString("Email"), rs.getString("Phone"), String.valueOf(rs.getDate("WarrantyEndDate")) });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 11: Retrieve all transactions that had a Total Amount greater than $500 to identify high-value online sales.
     */
    public static ObservableList<String[]> getHighValueTransactions() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT CustomerID, TransactionID, TotalAmount, OrderDate FROM Transactions WHERE TotalAmount > 500.00 ORDER BY TotalAmount DESC";
        System.out.println("[SQL-DAO] Executing Query 11 (High Value Purchases > $500)");
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{ "Cust: " + rs.getInt("CustomerID"), "Bill: " + rs.getInt("TransactionID"), String.format("$%.2f", rs.getDouble("TotalAmount")), String.valueOf(rs.getDate("OrderDate")) });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 17: Retrieve a list of all products bought by a specific customer that still have a valid, unexpired warranty (Warranty End Date > Current Date).
     */
    public static ObservableList<String[]> getCustomerActiveWarranties(int customerId) {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductName, w.WarrantyEndDate FROM Warranties w " +
                "JOIN Products p ON w.ProductID = p.ProductID " +
                "WHERE w.CustomerID = ? AND w.WarrantyEndDate > CURRENT_DATE";
        System.out.println("[SQL-DAO] Executing Query 17 (Valid warranties fetch)");
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{ rs.getString("ProductName"), String.valueOf(rs.getDate("WarrantyEndDate")) });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 19: Count the total number of active warranties currently registered in the system for the "Laptops" category.
     */
    public static int getActiveWarrantiesForLaptops() {
        String query = "SELECT COUNT(w.WarrantyID) FROM Warranties w " +
                "JOIN Products p ON w.ProductID = p.ProductID " +
                "JOIN Categories c ON p.CategoryID = c.CategoryID " +
                "WHERE c.CategoryName = 'Laptops' AND w.WarrantyEndDate > CURRENT_DATE";
        System.out.println("[SQL-DAO] Executing Query 19 (Active laptop warranties)");
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * QUERY 20: Calculate the total online sales revenue generated by a specific product across all transactions.
     */
    public static double getTotalRevenueByProduct(int productId) {
        String query = "SELECT SUM(Quantity * PriceAtSale) FROM Transaction_Items WHERE ProductID = ?";
        System.out.println("[SQL-DAO] Executing Query 20 for Target Product ID: " + productId);
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // ACID checkout base mechanics
    public static ObservableList<String[]> getCartItems(int customerId) {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductID, p.ProductName, p.UnitPrice, p.Discount, c.SelectedQuantity FROM Adds_to_Cart c JOIN Products p ON c.ProductID = p.ProductID WHERE c.CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    double price = rs.getDouble("UnitPrice"); double discount = rs.getDouble("Discount"); int qty = rs.getInt("SelectedQuantity");
                    double finalPrice = price * (1 - (discount / 100.0));
                    list.add(new String[] { rs.getString("ProductName"), String.valueOf(qty), String.format("%.2f", finalPrice), String.format("%.2f", finalPrice * qty), String.valueOf(rs.getInt("ProductID")) });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean executeACIDCheckout(int customerId, double totalAmount, String paymentMethod, ObservableList<String[]> cartItems) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection(); if (conn == null) return false;
            conn.setAutoCommit(false);
            int nextTxId = 1;
            String seqQuery = "SELECT COALESCE(MAX(TransactionID), 0) + 1 FROM Transactions WHERE CustomerID = ?";
            try (PreparedStatement psSeq = conn.prepareStatement(seqQuery)) {
                psSeq.setInt(1, customerId);
                try (ResultSet rs = psSeq.executeQuery()) { if (rs.next()) nextTxId = rs.getInt(1); }
            }
            String txQuery = "INSERT INTO Transactions (CustomerID, TransactionID, TotalAmount, PaymentMethod) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psTx = conn.prepareStatement(txQuery)) {
                psTx.setInt(1, customerId); psTx.setInt(2, nextTxId); psTx.setDouble(3, totalAmount); psTx.setString(4, paymentMethod);
                psTx.executeUpdate();
            }
            String itemQuery = "INSERT INTO Transaction_Items (CustomerID, TransactionID, ProductID, Quantity, PriceAtSale) VALUES (?, ?, ?, ?, ?)";
            String stockQuery = "UPDATE Stored_in SET StockQuantity = StockQuantity - ? WHERE ProductID = ? AND WarehouseID = 1";
            try (PreparedStatement psItem = conn.prepareStatement(itemQuery); PreparedStatement psStock = conn.prepareStatement(stockQuery)) {
                for (String[] row : cartItems) {
                    int prodId = Integer.parseInt(row[4]); int qty = Integer.parseInt(row[1]); double salePrice = Double.parseDouble(row[2]);
                    psItem.setInt(1, customerId); psItem.setInt(2, nextTxId); psItem.setInt(3, prodId); psItem.setInt(4, qty); psItem.setDouble(5, salePrice); psItem.executeUpdate();
                    psStock.setInt(1, qty); psStock.setInt(2, prodId);
                    if (psStock.executeUpdate() == 0) throw new SQLException("Shortage product ID: " + prodId);
                }
            }
            String clearCartQuery = "DELETE FROM Adds_to_Cart WHERE CustomerID = ?";
            try (PreparedStatement psClear = conn.prepareStatement(clearCartQuery)) { psClear.setInt(1, customerId); psClear.executeUpdate(); }
            conn.commit(); return true;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            return false;
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }
}