package com.example.dbsph4.models;

public class Customer {
    // Private fields representing database columns from Phase 2 report
    private int customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;

    // Default Constructor
    public Customer() {
    }

    // Constructor without ID (Used when registering/inserting a new customer into DB)
    public Customer(String firstName, String lastName, String email, String password, String phone, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    // Full Constructor (Used when retrieving existing customers from DB)
    public Customer(int customerId, String firstName, String lastName, String email, String password, String phone, String address) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters with Error Handling
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId > 0) {
            this.customerId = customerId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid CustomerID: " + customerId);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName;
        } else {
            System.err.println("[MODEL-ERROR] First name cannot be empty.");
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName;
        } else {
            System.err.println("[MODEL-ERROR] Last name cannot be empty.");
        }
    }

    // Helper method to get full name easily in GUI Tables
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email;
        } else {
            System.err.println("[MODEL-ERROR] Invalid email format: " + email);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Override toString for deep tracking and debugging
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}