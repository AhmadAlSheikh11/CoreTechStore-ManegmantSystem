package com.example.dbsph4.gui;

import com.example.dbsph4.dao.ProductDAO;
import com.example.dbsph4.database.SessionManager;
import com.example.dbsph4.models.Product;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProductController {

    private VBox viewLayout;
    private TableView<Product> productTable;
    private TextField txtSearch; // New search field linked to Query 5

    // Management Inputs for Owner UI
    private TextField txtName, txtPrice, txtDiscount, txtImage, txtCategory;
    private TextArea txtDescription;

    public ProductController() {
        viewLayout = new VBox(15);
        viewLayout.setPadding(new Insets(20));
        buildUI();
        refreshTableData(); // Loads initial data safely using DAO
    }

    public VBox getView() {
        return viewLayout;
    }

    private void buildUI() {
        Label titleLabel = new Label("💻 CoreTech Products Catalog");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        viewLayout.getChildren().add(titleLabel);

        // Advanced Search Bar (Connected to Query 5)
        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Live search product name...");
        txtSearch.setOnKeyReleased(e -> {
            String token = txtSearch.getText().trim();
            if (token.isEmpty()) {
                refreshTableData();
            } else {
                productTable.setItems(ProductDAO.searchProductsByName(token));
            }
        });
        viewLayout.getChildren().add(txtSearch);

        // Table initialization
        productTable = new TableView<>();
        setupTableColumns();
        viewLayout.getChildren().add(productTable);

        // Role Layout Routing
        if (SessionManager.isOwner()) {
            renderOwnerControls();
        } else if (SessionManager.isCustomer()) {
            renderCustomerControls();
        } else {
            Label guestLabel = new Label("💡 Login to purchase items and add them to your cart.");
            guestLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d;");
            viewLayout.getChildren().add(guestLabel);
        }
    }

    private void setupTableColumns() {
        TableColumn<Product, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colId.setPrefWidth(50);

        TableColumn<Product, String> colName = new TableColumn<>("Product Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colName.setPrefWidth(150);

        TableColumn<Product, Double> colPrice = new TableColumn<>("Unit Price ($)");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colPrice.setPrefWidth(90);

        TableColumn<Product, Double> colDiscount = new TableColumn<>("Discount (%)");
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colDiscount.setPrefWidth(90);

        TableColumn<Product, Integer> colCatId = new TableColumn<>("Category ID");
        colCatId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCatId.setPrefWidth(90);

        productTable.getColumns().addAll(colId, colName, colPrice, colDiscount, colCatId);
    }

    private void renderOwnerControls() {
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        txtName = new TextField(); txtName.setPromptText("Product Name");
        txtPrice = new TextField(); txtPrice.setPromptText("Price");
        txtDiscount = new TextField(); txtDiscount.setPromptText("Discount");
        txtCategory = new TextField(); txtCategory.setPromptText("Category ID");
        txtImage = new TextField(); txtImage.setPromptText("Image.png");
        txtDescription = new TextArea(); txtDescription.setPromptText("Description...");
        txtDescription.setPrefRowCount(2);

        grid.add(new Label("Name:"), 0, 0); grid.add(txtName, 1, 0);
        grid.add(new Label("Price:"), 2, 0); grid.add(txtPrice, 3, 0);
        grid.add(new Label("Discount:"), 0, 1); grid.add(txtDiscount, 1, 1);
        grid.add(new Label("Category ID:"), 2, 1); grid.add(txtCategory, 3, 1);
        grid.add(new Label("Image File:"), 0, 2); grid.add(txtImage, 1, 2);
        grid.add(new Label("Description:"), 0, 3); grid.add(txtDescription, 1, 3, 3, 1);

        Button btnInsert = new Button("➕ Insert Product");
        btnInsert.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnInsert.setOnAction(e -> handleInsert());

        Button btnUpdate = new Button("⚙️ Update Selected"); // New update UI button trigger
        btnUpdate.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        btnUpdate.setOnAction(e -> handleUpdate());

        Button btnDelete = new Button("🗑️ Delete Selected");
        btnDelete.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> handleDelete());

        HBox btnBox = new HBox(10, btnInsert, btnUpdate, btnDelete);

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtName.setText(newSel.getProductName());
                txtPrice.setText(String.valueOf(newSel.getUnitPrice()));
                txtDiscount.setText(String.valueOf(newSel.getDiscount()));
                txtCategory.setText(String.valueOf(newSel.getCategoryId()));
                txtImage.setText(newSel.getImageUrl());
                txtDescription.setText(newSel.getDescription());
            }
        });

        viewLayout.getChildren().addAll(grid, btnBox);
    }

    private void renderCustomerControls() {
        Button btnAddToCart = new Button("🛒 Add Selected Item to My Cart");
        btnAddToCart.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddToCart.setOnAction(e -> handleAddToCart());
        viewLayout.getChildren().add(btnAddToCart);
    }

    private void refreshTableData() {
        productTable.setItems(ProductDAO.getAllProducts());
    }

    private void handleInsert() {
        try {
            Product p = new Product(txtName.getText().trim(), txtDescription.getText().trim(),
                    Double.parseDouble(txtPrice.getText().trim()), Double.parseDouble(txtDiscount.getText().trim()),
                    txtImage.getText().trim(), Integer.parseInt(txtCategory.getText().trim()));
            if (ProductDAO.insertProduct(p)) {
                refreshTableData();
                clearInputFields();
            }
        } catch (Exception e) {
            System.err.println("[UI-ERROR] Data submission parsing error.");
        }
    }

    private void handleUpdate() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            selected.setProductName(txtName.getText().trim());
            selected.setDescription(txtDescription.getText().trim());
            selected.setUnitPrice(Double.parseDouble(txtPrice.getText().trim()));
            selected.setDiscount(Double.parseDouble(txtDiscount.getText().trim()));
            selected.setImageUrl(txtImage.getText().trim());
            selected.setCategoryId(Integer.parseInt(txtCategory.getText().trim()));

            if (ProductDAO.updateProduct(selected)) {
                refreshTableData();
                clearInputFields();
            }
        } catch (Exception e) {
            System.err.println("[UI-ERROR] Modification entry error.");
        }
    }

    private void handleDelete() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null && ProductDAO.deleteProduct(selected.getProductId())) {
            refreshTableData();
            clearInputFields();
        }
    }

    private void handleAddToCart() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null || !SessionManager.isCustomer()) return;

        int activeCustomerId = SessionManager.getCurrentCustomer().getCustomerId();


        String query = "INSERT INTO Adds_to_Cart (CustomerID, ProductID, SelectedQuantity) VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE SelectedQuantity = SelectedQuantity + 1";

        try (java.sql.Connection conn = com.example.dbsph4.database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, activeCustomerId);
            pstmt.setInt(2, selected.getProductId());
            if (pstmt.executeUpdate() > 0) {
                System.out.println("[SQL-SUCCESS] Product committed to active customer's table cart.");
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
    }

    private void clearInputFields() {
        if (txtName != null) {
            txtName.clear(); txtPrice.clear(); txtDiscount.clear();
            txtCategory.clear(); txtImage.clear(); txtDescription.clear();
        }
    }
}