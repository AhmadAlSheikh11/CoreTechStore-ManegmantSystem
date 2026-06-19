package com.example.dbsph4.dao;

import com.example.dbsph4.database.DatabaseConnection;
import com.example.dbsph4.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class CustomerDAO {

    /**
     * BASE QUERY: Validate customer email and password during login.
     */
    public static Customer authenticateCustomer(String email, String password) {
        String query = "SELECT * FROM Customers WHERE Email = ? AND Password = ?";
        System.out.println("[SQL-DAO] Authenticating: " + email);
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
            System.err.println("[SQL-DAO-ERROR] Authentication query failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * BASE QUERY: Register a new customer profile — INSERT into Customers table.
     */
    public static boolean registerCustomer(Customer customer) {
        String query = "INSERT INTO Customers (FirstName, LastName, Email, Password, Phone, Address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        System.out.println("[SQL-DAO] Registering customer: " + customer.getEmail());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPassword());
            pstmt.setString(5, customer.getPhone());
            pstmt.setString(6, customer.getAddress());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[SQL-DAO] Customer inserted successfully: " + customer.getEmail());
                return true;
            }
            System.err.println("[SQL-DAO-ERROR] INSERT returned 0 rows for: " + customer.getEmail());
            return false;
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] registerCustomer failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * BASE QUERY: Fetch all customers — SELECT from Customers table.
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Customers";
        System.out.println("[SQL-DAO] Fetching all customers.");
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
            System.out.println("[SQL-DAO] Loaded " + list.size() + " customer(s).");
        } catch (SQLException e) {
            System.err.println("[SQL-DAO-ERROR] getAllCustomers failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}