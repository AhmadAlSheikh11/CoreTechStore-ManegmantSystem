package com.example.dbsph4.gui;

import com.example.dbsph4.dao.WarehouseDAO;
import com.example.dbsph4.models.Warehouse;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class WarehouseController {

    private VBox viewLayout;
    private TableView<Warehouse> warehouseTable;
    private TableView<String[]> stockTable;
    private ObservableList<String[]> stockList;

    // Input fields for Adding and Updating Stocks
    private TextField txtWHName, txtWHLocation, txtNewStockQty;
    private int currentSelectedWarehouseId = -1;

    public WarehouseController() {
        viewLayout = new VBox(15);
        viewLayout.setPadding(new Insets(20));

        buildUI();
        refreshWarehouseTable();
    }

    public VBox getView() {
        return viewLayout;
    }

    private void buildUI() {
        Label titleLabel = new Label("🏭 Warehouses Management Central Dashboard");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        viewLayout.getChildren().add(titleLabel);

        // 1. Primary Warehouses Table Setup
        warehouseTable = new TableView<>();
        setupWarehouseTableColumns();
        viewLayout.getChildren().add(warehouseTable);

        // 2. Sub-Table for Stock Items View (Displays JOIN analysis via DAO)
        Label stockTitle = new Label("📦 Live Stock Levels Inside Selection:");
        stockTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        viewLayout.getChildren().add(stockTitle);

        stockTable = new TableView<>();
        setupStockTableColumns();
        viewLayout.getChildren().add(stockTable);

        // Row Listener to reload stock levels using Relational JOIN from DAO
        warehouseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                currentSelectedWarehouseId = newSel.getWarehouseId();
                refreshStockTable(currentSelectedWarehouseId);
            }
        });

        // 3. Administrative Operational Forms Layout (Insert and Stock Update Controls)
        renderAdminOperationsForms();
    }

    private void setupWarehouseTableColumns() {
        TableColumn<Warehouse, Integer> colId = new TableColumn<>("Warehouse ID");
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("warehouseId"));
        colId.setPrefWidth(100);

        TableColumn<Warehouse, String> colName = new TableColumn<>("Warehouse Facility Name");
        colName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("warehouseName"));
        colName.setPrefWidth(200);

        TableColumn<Warehouse, String> colLoc = new TableColumn<>("City / Location");
        colLoc.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("location"));
        colLoc.setPrefWidth(200);

        warehouseTable.getColumns().addAll(colId, colName, colLoc);
    }

    private void setupStockTableColumns() {
        TableColumn<String[], String> colProdName = new TableColumn<>("Product Name");
        colProdName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[0]));
        colProdName.setPrefWidth(220);

        TableColumn<String[], String> colQty = new TableColumn<>("Current Quantity In Stock");
        colQty.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[1]));
        colQty.setPrefWidth(160);

        stockTable.getColumns().addAll(colProdName, colQty);
    }

    private void renderAdminOperationsForms() {
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10);

        // Facility Add Forms Setup
        txtWHName = new TextField(); txtWHName.setPromptText("Facility Name");
        txtWHLocation = new TextField(); txtWHLocation.setPromptText("City / Address");
        Button btnAddWH = new Button("Add Facility");
        btnAddWH.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnAddWH.setOnAction(e -> handleAddWarehouse());

        grid.add(new Label("New Warehouse Name:"), 0, 0); grid.add(txtWHName, 1, 0);
        grid.add(new Label("Location:"), 2, 0); grid.add(txtWHLocation, 3, 0);
        grid.add(btnAddWH, 4, 0);

        // Stock Level Supply Chain Replenishment Control Setup (Query 13 UI Integration)
        txtNewStockQty = new TextField(); txtNewStockQty.setPromptText("Set New Quantity");
        Button btnUpdateStock = new Button("⚙️ Update Stock Quantity");
        btnUpdateStock.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        btnUpdateStock.setOnAction(e -> handleStockLevelUpdate());

        grid.add(new Label("Selected Product Stock Modification:"), 0, 1);
        grid.add(txtNewStockQty, 1, 1);
        grid.add(btnUpdateStock, 2, 1);

        viewLayout.getChildren().add(grid);
    }

    private void refreshWarehouseTable() {
        warehouseTable.setItems(WarehouseDAO.getAllWarehouses());
    }

    private void refreshStockTable(int warehouseId) {
        stockList = WarehouseDAO.getStockLevels(warehouseId);
        stockTable.setItems(stockList);
    }

    private void handleAddWarehouse() {
        String name = txtWHName.getText().trim();
        String loc = txtWHLocation.getText().trim();
        if (!name.isEmpty() && !loc.isEmpty() && WarehouseDAO.insertWarehouse(name, loc)) {
            refreshWarehouseTable();
            txtWHName.clear(); txtWHLocation.clear();
        }
    }

    private void handleStockLevelUpdate() {
        String[] selectedStockRow = stockTable.getSelectionModel().getSelectedItem();
        if (selectedStockRow == null || currentSelectedWarehouseId == -1) {
            System.err.println("[UI-WARN] Modification ignored: No stock product highlighted inside sub-table.");
            return;
        }

        try {
            int targetProductId = Integer.parseInt(selectedStockRow[2]); // Fetch hidden ProductID from array
            int newQuantity = Integer.parseInt(txtNewStockQty.getText().trim());

            if (WarehouseDAO.updateStockQuantity(currentSelectedWarehouseId, targetProductId, newQuantity)) {
                System.out.println("[UI-SUCCESS] Warehouse product storage levels synchronized seamlessly.");
                refreshStockTable(currentSelectedWarehouseId); // Instantly update view tables
                txtNewStockQty.clear();
            }
        } catch (Exception e) {
            System.err.println("[UI-ERROR] Failure processing numerical entry input formatting checks.");
        }
    }
}