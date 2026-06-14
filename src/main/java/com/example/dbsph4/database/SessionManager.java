package com.example.dbsph4.database;

import com.example.dbsph4.models.Customer;

public class SessionManager {

    // Enum to define user roles inside the application
    public enum Role { GUEST, CUSTOMER, OWNER }

    private static Role currentRole = Role.GUEST;
    private static Customer currentCustomer = null; // Stores logged-in customer data

    public static Role getCurrentRole() {
        return currentRole;
    }

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Sets the session to a logged-in Customer
     */
    public static void loginAsCustomer(Customer customer) {
        currentRole = Role.CUSTOMER;
        currentCustomer = customer;
        System.out.println("[SESSION-SUCCESS] User logged in as Customer: " + customer.getFullName());
    }

    /**
     * Sets the session to Owner/Admin
     */
    public static void loginAsOwner() {
        currentRole = Role.OWNER;
        currentCustomer = null;
        System.out.println("[SESSION-SUCCESS] Logged in as STORE OWNER / ADMIN.");
    }

    /**
     * Resets the session back to Guest mode
     */
    public static void logout() {
        currentRole = Role.GUEST;
        currentCustomer = null;
        System.out.println("[SESSION-TRACE] User logged out. Switched to GUEST mode.");
    }

    public static boolean isGuest() {
        return currentRole == Role.GUEST;
    }

    public static boolean isCustomer() {
        return currentRole == Role.CUSTOMER;
    }

    public static boolean isOwner() {
        return currentRole == Role.OWNER;
    }
}