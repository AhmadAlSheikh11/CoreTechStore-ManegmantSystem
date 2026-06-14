package com.example.dbsph4.dao;

import com.example.dbsph4.database.DatabaseConnection;
import com.example.dbsph4.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CustomerDAO {

    /**
     * QUERY 8: SELECT * FROM Customers WHERE Email = ? AND Password = ?
     * Validates customer credentials securely during Sign In.
     */
    public static Customer authenticateCustomer(String email, String password) {
        String query = "SELECT * FROM Customers WHERE Email = ? AND Password = ?";
        System.out.println("[SQL-DAO] Validating credentials for: " + email);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("CustomerID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            rs.getString("Phone"),
                            rs.getString("Address")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] Authentication query failed.");
            e.printStackTrace();
        }
        return null; // Returns null if no match found safely
    }

    /**
     * QUERY 9: INSERT INTO Customers
     * Registers a new customer profile into the store network.
     */
    public static boolean registerCustomer(Customer customer) {
        String query = "INSERT INTO Customers (FirstName, LastName, Email, Password, Phone, Address) VALUES (?, ?, ?, ?, ?, ?)";
        System.out.println("[SQL-DAO] Registering new customer email: " + customer.getEmail());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPassword());
            pstmt.setString(5, customer.getPhone());
            pstmt.setString(6, customer.getAddress());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] Customer registration failed.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * QUERY 10: SELECT * FROM Customers (New Added Query for Owner)
     * Fetches all registered customer profiles for administrative overview.
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Customers";
        System.out.println("[SQL-DAO] Executing administrative query: " + query);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("CustomerID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Password"),
                        rs.getString("Phone"),
                        rs.getString("Address")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] Failed to compile customer subscriptions list.");
            e.printStackTrace();
        }
        return list;
    }
}