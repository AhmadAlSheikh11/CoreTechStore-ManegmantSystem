package com.example.dbsph4.models;

public class Warranty {
    private int warrantyId;
    private int customerId;
    private int transactionId;
    private int productId;
    private String startDate;    // Matches StartDate from SQL schema
    private String endDate;      // Matches EndDate from SQL schema
    private String warrantyStatus;

    public Warranty() {
    }

    public Warranty(int customerId, int transactionId, int productId, String startDate, String endDate, String warrantyStatus) {
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.warrantyStatus = warrantyStatus;
    }

    public Warranty(int warrantyId, int customerId, int transactionId, int productId, String startDate, String endDate, String warrantyStatus) {
        this.warrantyId = warrantyId;
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.warrantyStatus = warrantyStatus;
    }

    public int getWarrantyId() { return warrantyId; }
    public void setWarrantyId(int warrantyId) { this.warrantyId = warrantyId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getWarrantyStatus() { return warrantyStatus; }
    public void setWarrantyStatus(String warrantyStatus) { this.warrantyStatus = warrantyStatus; }

    @Override
    public String toString() {
        return "Warranty{" + "warrantyId=" + warrantyId + ", customerId=" + customerId + ", transactionId=" + transactionId + ", productId=" + productId + ", startDate='" + startDate + '\'' + ", endDate='" + endDate + '\'' + ", warrantyStatus='" + warrantyStatus + '\'' + '}';
    }
}