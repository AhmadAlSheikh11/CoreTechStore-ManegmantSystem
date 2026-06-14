package com.example.dbsph4.models;

public class Warranty {
    // Private fields representing database columns
    private int warrantyId;      // Primary Key (Auto-Increment)
    private int customerId;      // Part of Composite Foreign Key to Transaction_Items
    private int transactionId;   // Part of Composite Foreign Key to Transaction_Items
    private int productId;       // Part of Composite Foreign Key to Transaction_Items
    private String startDate;    // Kept as String for easy formatting and handling
    private String endDate;      // Kept as String for easy formatting and handling
    private String warrantyStatus;

    // Default Constructor
    public Warranty() {
    }

    // Constructor without WarrantyID (Used for inserting a new warranty into DB)
    public Warranty(int customerId, int transactionId, int productId, String startDate, String endDate, String warrantyStatus) {
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.warrantyStatus = warrantyStatus;
    }

    // Full Constructor (Used when pulling existing warranty data from DB)
    public Warranty(int warrantyId, int customerId, int transactionId, int productId, String startDate, String endDate, String warrantyStatus) {
        this.warrantyId = warrantyId;
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.warrantyStatus = warrantyStatus;
    }

    // Getters and Setters with basic error checking
    public int getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(int warrantyId) {
        if (warrantyId > 0) {
            this.warrantyId = warrantyId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid WarrantyID: " + warrantyId);
        }
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId > 0) {
            this.customerId = customerId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid CustomerID in Warranty: " + customerId);
        }
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        if (transactionId > 0) {
            this.transactionId = transactionId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid TransactionID in Warranty: " + transactionId);
        }
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        if (productId > 0) {
            this.productId = productId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid ProductID in Warranty: " + productId);
        }
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getWarrantyStatus() {
        return warrantyStatus;
    }

    public void setWarrantyStatus(String warrantyStatus) {
        if (warrantyStatus != null && !warrantyStatus.trim().isEmpty()) {
            this.warrantyStatus = warrantyStatus;
        } else {
            System.err.println("[MODEL-ERROR] Warranty status cannot be empty.");
        }
    }

    // Override toString for clear step-by-step console debugging
    @Override
    public String toString() {
        return "Warranty{" +
                "warrantyId=" + warrantyId +
                ", customerId=" + customerId +
                ", transactionId=" + transactionId +
                ", productId=" + productId +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", warrantyStatus='" + warrantyStatus + '\'' +
                '}';
    }
}