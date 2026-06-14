package com.example.dbsph4.models;

public class Product {
    // Private fields representing database columns from Phase 2 report
    private int productId;
    private String productName;
    private String description;
    private double unitPrice;
    private double discount;
    private String imageUrl;
    private int categoryId; // Foreign Key referencing Categories

    // Default Constructor
    public Product() {
    }

    // Constructor without ID (Used for inserting a new product into DB)
    public Product(String productName, String description, double unitPrice, double discount, String imageUrl, int categoryId) {
        this.productName = productName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }

    // Full Constructor (Used when retrieving existing products from DB)
    public Product(int productId, String productName, String description, double unitPrice, double discount, String imageUrl, int categoryId) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }

    // Getters and Setters with strict error checking
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        if (productId > 0) {
            this.productId = productId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid ProductID: " + productId);
        }
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        if (productName != null && !productName.trim().isEmpty()) {
            this.productName = productName;
        } else {
            System.err.println("[MODEL-ERROR] Product name cannot be empty.");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice >= 0) {
            this.unitPrice = unitPrice;
        } else {
            System.err.println("[MODEL-ERROR] Unit price cannot be negative: " + unitPrice);
        }
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        if (discount >= 0.0 && discount <= 100.0) {
            this.discount = discount;
        } else {
            System.err.println("[MODEL-ERROR] Discount must be between 0 and 100: " + discount);
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        if (categoryId > 0) {
            this.categoryId = categoryId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid CategoryID reference: " + categoryId);
        }
    }

    // Override toString for deep console tracking
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", unitPrice=" + unitPrice +
                ", discount=" + discount +
                ", categoryId=" + categoryId +
                '}';
    }
}