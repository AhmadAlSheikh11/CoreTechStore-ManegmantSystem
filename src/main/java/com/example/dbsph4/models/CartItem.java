package com.example.dbsph4.models;

public class CartItem {
    // Private fields representing composite keys and attributes from database
    private int customerId;
    private int productId;
    private int selectedQuantity;
    private String dateAdded; // Kept as String/Timestamp for easy formatting in GUI

    // Default Constructor
    public CartItem() {
    }

    // Full Constructor (Matches the database schema exactly)
    public CartItem(int customerId, int productId, int selectedQuantity, String dateAdded) {
        this.customerId = customerId;
        this.productId = productId;
        this.selectedQuantity = selectedQuantity;
        this.dateAdded = dateAdded;
    }

    // Getters and Setters with error checks
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId > 0) {
            this.customerId = customerId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid CustomerID in CartItem: " + customerId);
        }
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        if (productId > 0) {
            this.productId = productId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid ProductID in CartItem: " + productId);
        }
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        if (selectedQuantity > 0) {
            this.selectedQuantity = selectedQuantity;
        } else {
            System.err.println("[MODEL-ERROR] Selected quantity must be greater than 0: " + selectedQuantity);
        }
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    // Override toString for clear step-by-step console debugging
    @Override
    public String toString() {
        return "CartItem{" +
                "customerId=" + customerId +
                ", productId=" + productId +
                ", selectedQuantity=" + selectedQuantity +
                ", dateAdded='" + dateAdded + '\'' +
                '}';
    }
}