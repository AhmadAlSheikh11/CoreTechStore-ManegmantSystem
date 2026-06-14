package com.example.dbsph4.models;

public class Transaction {
    // Private fields representing database columns (Reflecting Weak Entity structure)
    private int customerId;     // Part of Composite Primary Key & Foreign Key
    private int transactionId;  // Part of Composite Primary Key
    private String transactionDate; // Stores the DATETIME as a formatted String
    private double totalAmount;
    private String paymentMethod;

    // Default Constructor
    public Transaction() {
    }

    // Constructor without TransactionID (Used when creating a new bill, ID is determined per customer or globally)
    public Transaction(int customerId, double totalAmount, String paymentMethod) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

    // Full Constructor (Used when pulling transaction history from DB)
    public Transaction(int customerId, int transactionId, String transactionDate, double totalAmount, String paymentMethod) {
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters with Error Handling
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId > 0) {
            this.customerId = customerId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid CustomerID in Transaction: " + customerId);
        }
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        if (transactionId > 0) {
            this.transactionId = transactionId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid TransactionID: " + transactionId);
        }
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount >= 0) {
            this.totalAmount = totalAmount;
        } else {
            System.err.println("[MODEL-ERROR] Total amount cannot be negative: " + totalAmount);
        }
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
            this.paymentMethod = paymentMethod;
        } else {
            System.err.println("[MODEL-ERROR] Payment method cannot be empty.");
        }
    }

    // Override toString for clear step-by-step console debugging
    @Override
    public String toString() {
        return "Transaction{" +
                "customerId=" + customerId +
                ", transactionId=" + transactionId +
                ", transactionDate='" + transactionDate + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}