package com.example.dbsph4.models;

public class Category {
    // Private fields representing database columns
    private int categoryId;
    private String categoryName;
    private int warrantyMonths; // NEW: per-category warranty duration in months

    // Default Constructor
    public Category() {
    }

    // Constructor without ID (Used for inserting new categories into DB)
    public Category(String categoryName, int warrantyMonths) {
        this.categoryName = categoryName;
        this.warrantyMonths = warrantyMonths;
    }

    // Full Constructor (Used when retrieving data from DB)
    public Category(int categoryId, String categoryName, int warrantyMonths) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.warrantyMonths = warrantyMonths;
    }

    // Getters and Setters with basic error checking
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        if (categoryId > 0) {
            this.categoryId = categoryId;
        } else {
            System.err.println("[MODULE-ERROR] Invalid CategoryID value: " + categoryId);
        }
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            this.categoryName = categoryName;
        } else {
            System.err.println("[MODULE-ERROR] Category name cannot be null or empty.");
        }
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        if (warrantyMonths >= 0) {
            this.warrantyMonths = warrantyMonths;
        } else {
            System.err.println("[MODULE-ERROR] Warranty months cannot be negative: " + warrantyMonths);
        }
    }

    // Override toString for clear logging and step-by-step trace
    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", warrantyMonths=" + warrantyMonths +
                '}';
    }
}