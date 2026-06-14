package com.example.dbsph4.models;

public class Category {
    // Private fields representing database columns
    private int categoryId;
    private String categoryName;

    // Default Constructor
    public Category() {
    }

    // Constructor without ID (Used for inserting new categories into DB)
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    // Full Constructor (Used when retrieving data from DB)
    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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

    // Override toString for clear logging and step-by-step trace
    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}