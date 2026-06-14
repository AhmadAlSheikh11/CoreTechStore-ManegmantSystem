package com.example.dbsph4.models;

public class TransactionItem {
    // Private fields representing the composite foreign keys and attributes
    private int customerId;    // From Transactions composite key
    private int transactionId; // From Transactions composite key
    private int productId;     // Foreign key referencing Products
    private int quantity;
    private double priceAtSale;

    // Default Constructor
    public TransactionItem() {
    }

    // Full Constructor (Matches the database schema exactly)
    public TransactionItem(int customerId, int transactionId, int productId, int quantity, double priceAtSale) {
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
    }

    // Getters and Setters with Error Handling
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId > 0) {
            this.customerId = customerId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid CustomerID in TransactionItem: " + customerId);
        }
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        if (transactionId > 0) {
            this.transactionId = transactionId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid TransactionID in TransactionItem: " + transactionId);
        }
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        if (productId > 0) {
            this.productId = productId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid ProductID in TransactionItem: " + productId);
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        } else {
            System.err.println("[MODEL-ERROR] Quantity must be greater than 0: " + quantity);
        }
    }

    public double getPriceAtSale() {
        return priceAtSale;
    }

    public void setPriceAtSale(double priceAtSale) {
        if (priceAtSale >= 0) {
            this.priceAtSale = priceAtSale;
        } else {
            System.err.println("[MODEL-ERROR] Price at sale cannot be negative: " + priceAtSale);
        }
    }

    // Override toString for clear step-by-step console debugging
    @Override
    public String toString() {
        return "TransactionItem{" +
                "customerId=" + customerId +
                ", transactionId=" + transactionId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", priceAtSale=" + priceAtSale +
                '}';
    }
}