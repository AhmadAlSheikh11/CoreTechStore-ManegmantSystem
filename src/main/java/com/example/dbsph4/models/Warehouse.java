package com.example.dbsph4.models;

public class Warehouse {
    // Private fields representing database columns
    private int warehouseId;
    private String warehouseName;
    private String location;

    // Default Constructor
    public Warehouse() {
    }

    // Constructor without ID (Used for adding a new warehouse into DB)
    public Warehouse(String warehouseName, String location) {
        this.warehouseName = warehouseName;
        this.location = location;
    }

    // Full Constructor (Used when retrieving data from DB)
    public Warehouse(int warehouseId, String warehouseName, String location) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.location = location;
    }

    // Getters and Setters with basic error logs
    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        if (warehouseId > 0) {
            this.warehouseId = warehouseId;
        } else {
            System.err.println("[MODEL-ERROR] Invalid WarehouseID: " + warehouseId);
        }
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        if (warehouseName != null && !warehouseName.trim().isEmpty()) {
            this.warehouseName = warehouseName;
        } else {
            System.err.println("[MODEL-ERROR] Warehouse name cannot be empty.");
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location != null && !location.trim().isEmpty()) {
            this.location = location;
        } else {
            System.err.println("[MODEL-ERROR] Warehouse location cannot be empty.");
        }
    }

    // Override toString for clear logging and step-by-step trace
    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseId=" + warehouseId +
                ", warehouseName='" + warehouseName + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}