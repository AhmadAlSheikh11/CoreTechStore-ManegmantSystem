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
        String query = "SELECT ti.PriceAtSale, w.EndDate FROM Transaction_Items ti " +
                "LEFT JOIN Warranties w ON ti.CustomerID = w.CustomerID AND ti.ProductID = w.ProductID AND ti.TransactionID = w.TransactionID " +
                "WHERE ti.CustomerID = ? AND ti.TransactionID = ? AND ti.ProductID = ?";
        System.out.println("[SQL-DAO] Executing Query 3 for sales verification");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, transactionId);
            pstmt.setInt(3, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date endDate = rs.getDate("EndDate");
                    String warrantyStr = endDate != null ? endDate.toString() : "No warranty registered";
                    return "Price at Sale: $" + String.format("%.2f", rs.getDouble("PriceAtSale")) +
                            " | Warranty End: " + warrantyStr;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "No sales record found.";
    }

    /**
     * QUERY 4: List all products, their purchased quantities, and warranty expiration dates for the most recent transaction belonging to the email (like: "Ahmad@gmail.com").
     */
    public static ObservableList<String[]> getRecentOrderDetailsByEmail(String email) {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductName, ti.Quantity, w.EndDate FROM Transaction_Items ti " +
                "JOIN Products p ON ti.ProductID = p.ProductID " +
                "JOIN Customers c ON ti.CustomerID = c.CustomerID " +
                "LEFT JOIN Warranties w ON ti.CustomerID = w.CustomerID AND ti.ProductID = w.ProductID AND ti.TransactionID = w.TransactionID " +
                "WHERE c.Email = ? AND ti.TransactionID = (SELECT MAX(TransactionID) FROM Transactions WHERE CustomerID = c.CustomerID)";
        System.out.println("[SQL-DAO] Executing Query 4 for email: " + email);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date endDate = rs.getDate("EndDate");
                    list.add(new String[]{
                            rs.getString("ProductName"),
                            String.valueOf(rs.getInt("Quantity")),
                            endDate != null ? endDate.toString() : "No warranty"
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 5: Calculate the total online sales revenue generated across the entire E-commerce platform for the current month.
     */
    public static double getCurrentMonthRevenue() {
        String query = "SELECT SUM(TotalAmount) FROM Transactions WHERE MONTH(TransactionDate) = MONTH(CURRENT_DATE) AND YEAR(TransactionDate) = YEAR(CURRENT_DATE)";
        System.out.println("[SQL-DAO] Executing Query 5 (Monthly Revenue)");
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
        String query = "SELECT TransactionID, TransactionDate, TotalAmount FROM Transactions WHERE CustomerID = ? ORDER BY TransactionID DESC LIMIT 1";
        System.out.println("[SQL-DAO] Executing Query 6 for Customer ID: " + customerId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "Transaction ID: " + rs.getInt("TransactionID") +
                            " | Date: " + rs.getTimestamp("TransactionDate") +
                            " | Total: $" + rs.getDouble("TotalAmount");
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
        String query = "SELECT t.CustomerID, t.TransactionID, t.TransactionDate FROM Transaction_Items ti " +
                "JOIN Transactions t ON ti.CustomerID = t.CustomerID AND ti.TransactionID = t.TransactionID " +
                "WHERE ti.ProductID = ?";
        System.out.println("[SQL-DAO] Executing Query 7 for Product ID: " + productId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            String.valueOf(rs.getInt("CustomerID")),
                            String.valueOf(rs.getInt("TransactionID")),
                            String.valueOf(rs.getDate("TransactionDate"))
                    });
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
        System.out.println("[SQL-DAO] Executing Query 8 (Top 3 Revenue Products)");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("ProductName"),
                        String.format("$%.2f", rs.getDouble("GrossProfit"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 9: Calculate the average number of items purchased per online transaction.
     */
    public static double getAverageItemsPerTransaction() {
        String query = "SELECT AVG(TotalItems) FROM (SELECT COUNT(ProductID) as TotalItems FROM Transaction_Items GROUP BY CustomerID, TransactionID) as OrderCounts";
        System.out.println("[SQL-DAO] Executing Query 9 (Average Items Per Transaction)");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    /**
     * QUERY 10: Retrieve the names, emails, and phone numbers of all customers whose product warranties are expiring within the next 30 days (for targeted support).
     */
    public static ObservableList<String[]> getWarrantiesExpiringIn30Days() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT c.FirstName, c.LastName, c.Email, c.Phone, w.EndDate FROM Warranties w " +
                "JOIN Customers c ON w.CustomerID = c.CustomerID " +
                "WHERE w.EndDate BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY)";
        System.out.println("[SQL-DAO] Executing Query 10 (Warranties Expiring in 30 Days)");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("FirstName") + " " + rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        String.valueOf(rs.getDate("EndDate"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 11: Retrieve all transactions that had a Total Amount greater than $500 to identify high-value online sales.
     */
    public static ObservableList<String[]> getHighValueTransactions() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT CustomerID, TransactionID, TotalAmount, TransactionDate FROM Transactions WHERE TotalAmount > 500.00 ORDER BY TotalAmount DESC";
        System.out.println("[SQL-DAO] Executing Query 11 (High-Value Transactions > $500)");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("CustomerID")),
                        String.valueOf(rs.getInt("TransactionID")),
                        String.format("$%.2f", rs.getDouble("TotalAmount")),
                        String.valueOf(rs.getDate("TransactionDate"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * QUERY 17: Retrieve a list of all products bought by a specific customer that still have a valid, unexpired warranty (Warranty End Date > Current Date).
     */
    public static ObservableList<String[]> getCustomerActiveWarranties(int customerId) {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductName, w.EndDate FROM Warranties w " +
                "JOIN Products p ON w.ProductID = p.ProductID " +
                "WHERE w.CustomerID = ? AND w.EndDate > CURRENT_DATE";
        System.out.println("[SQL-DAO] Executing Query 17 for Customer ID: " + customerId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("ProductName"),
                            String.valueOf(rs.getDate("EndDate"))
                    });
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
                "WHERE c.CategoryName = 'Laptops' AND w.EndDate > CURRENT_DATE";
        System.out.println("[SQL-DAO] Executing Query 19 (Active Laptop Warranties)");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * QUERY 20: Calculate the total online sales revenue generated by a specific product across all transactions.
     */
    public static double getTotalRevenueByProduct(int productId) {
        String query = "SELECT SUM(Quantity * PriceAtSale) FROM Transaction_Items WHERE ProductID = ?";
        System.out.println("[SQL-DAO] Executing Query 20 for Product ID: " + productId);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // ── Cart & Checkout (base operations) ────────────────────────────────────

    public static ObservableList<String[]> getCartItems(int customerId) {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String query = "SELECT p.ProductID, p.ProductName, p.UnitPrice, p.Discount, c.SelectedQuantity " +
                "FROM Adds_to_Cart c JOIN Products p ON c.ProductID = p.ProductID WHERE c.CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    double price = rs.getDouble("UnitPrice");
                    double discount = rs.getDouble("Discount");
                    int qty = rs.getInt("SelectedQuantity");
                    double finalPrice = price * (1 - (discount / 100.0));
                    list.add(new String[]{
                            rs.getString("ProductName"),
                            String.valueOf(qty),
                            String.format("%.2f", finalPrice),
                            String.format("%.2f", finalPrice * qty),
                            String.valueOf(rs.getInt("ProductID"))
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * ACID checkout: creates a Transaction, one Transaction_Items row per cart item,
     * decrements Stored_in stock, creates a Warranty per item (duration taken from
     * that product's Category.WarrantyMonths — e.g. Laptops vs Phones differ),
     * and clears the cart. All steps run inside a single transaction — if any
     * step fails, everything rolls back.
     */
    public static boolean executeACIDCheckout(int customerId, double totalAmount, String paymentMethod, ObservableList<String[]> cartItems) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            // ── Step 1: Determine next TransactionID for this customer ────────
            int nextTxId = 1;
            String seqQuery = "SELECT COALESCE(MAX(TransactionID), 0) + 1 FROM Transactions WHERE CustomerID = ?";
            try (PreparedStatement psSeq = conn.prepareStatement(seqQuery)) {
                psSeq.setInt(1, customerId);
                try (ResultSet rs = psSeq.executeQuery()) {
                    if (rs.next()) nextTxId = rs.getInt(1);
                }
            }

            // ── Step 2: INSERT into Transactions ─────────────────────────────
            String txQuery = "INSERT INTO Transactions (CustomerID, TransactionID, TotalAmount, PaymentMethod) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psTx = conn.prepareStatement(txQuery)) {
                psTx.setInt(1, customerId);
                psTx.setInt(2, nextTxId);
                psTx.setDouble(3, totalAmount);
                psTx.setString(4, paymentMethod);
                psTx.executeUpdate();
            }
            System.out.println("[SQL-DAO] Transaction created: CustomerID=" + customerId + " TxID=" + nextTxId);

            // ── Step 3: For each cart item: INSERT Transaction_Items,
            //            UPDATE Stored_in stock, INSERT Warranties
            //            (duration depends on the product's CATEGORY) ───────
            String itemQuery    = "INSERT INTO Transaction_Items (CustomerID, TransactionID, ProductID, Quantity, PriceAtSale) VALUES (?, ?, ?, ?, ?)";
            String stockQuery   = "UPDATE Stored_in SET StockQuantity = StockQuantity - ? WHERE ProductID = ? AND WarehouseID = 1";
            // EndDate computed in Java using the category's WarrantyMonths, not a fixed interval
            String warrantyQuery = "INSERT INTO Warranties (CustomerID, TransactionID, ProductID, StartDate, EndDate, WarrantyStatus) " +
                    "VALUES (?, ?, ?, CURRENT_DATE, ?, 'Active')";

            try (PreparedStatement psItem     = conn.prepareStatement(itemQuery);
                 PreparedStatement psStock    = conn.prepareStatement(stockQuery);
                 PreparedStatement psWarranty = conn.prepareStatement(warrantyQuery)) {

                for (String[] row : cartItems) {
                    int    prodId    = Integer.parseInt(row[4]);
                    int    qty       = Integer.parseInt(row[1]);
                    double salePrice = Double.parseDouble(row[2]);

                    // 3a. Insert Transaction_Items row
                    psItem.setInt(1, customerId);
                    psItem.setInt(2, nextTxId);
                    psItem.setInt(3, prodId);
                    psItem.setInt(4, qty);
                    psItem.setDouble(5, salePrice);
                    psItem.executeUpdate();

                    // 3b. Decrement stock in Stored_in (warehouse 1)
                    psStock.setInt(1, qty);
                    psStock.setInt(2, prodId);
                    if (psStock.executeUpdate() == 0) {
                        throw new SQLException("Insufficient stock for Product ID: " + prodId);
                    }

                    // 3c. Insert Warranty for this product.
                    //     Duration comes from this product's Category.WarrantyMonths
                    //     (e.g. Laptops=24mo, Phones=12mo) — NOT a flat rule for all products.
                    int warrantyMonths = com.example.dbsph4.dao.CategoryDAO.getWarrantyMonthsForProduct(prodId);
                    java.time.LocalDate endLocalDate = java.time.LocalDate.now().plusMonths(warrantyMonths);
                    java.sql.Date endDate = java.sql.Date.valueOf(endLocalDate);

                    psWarranty.setInt(1, customerId);
                    psWarranty.setInt(2, nextTxId);
                    psWarranty.setInt(3, prodId);
                    psWarranty.setDate(4, endDate);
                    psWarranty.executeUpdate();
                    System.out.println("[SQL-DAO] Warranty created: CustomerID=" + customerId +
                            " TxID=" + nextTxId + " ProductID=" + prodId +
                            " Duration=" + warrantyMonths + "mo EndDate=" + endDate);
                }
            }

            // ── Step 4: Clear the customer's cart ─────────────────────────────
            String clearCartQuery = "DELETE FROM Adds_to_Cart WHERE CustomerID = ?";
            try (PreparedStatement psClear = conn.prepareStatement(clearCartQuery)) {
                psClear.setInt(1, customerId);
                psClear.executeUpdate();
            }

            conn.commit();
            System.out.println("[SQL-DAO] Checkout committed: Transaction, Items, Stock, Warranties, Cart cleared.");
            return true;

        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] Checkout failed, rolling back: " + e.getMessage());
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
}