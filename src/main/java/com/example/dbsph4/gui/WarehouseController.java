package com.example.dbsph4.gui;

import com.example.dbsph4.dao.WarehouseDAO;
import com.example.dbsph4.dao.CategoryDAO;
import com.example.dbsph4.dao.ProductDAO;
import com.example.dbsph4.models.Warehouse;
import com.example.dbsph4.models.Category;
import com.example.dbsph4.models.Product;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

public class WarehouseController {

    private ScrollPane viewLayout;
    private VBox innerLayout;
    private TableView<Warehouse> warehouseTable;
    private TableView<String[]> stockTable;
    private ObservableList<String[]> stockList;

    private TextField txtWHName, txtWHLocation, txtNewStockQty;
    private int currentSelectedWarehouseId = -1;
    private Label lblStatus;

    private ComboBox<Category> comboAdminCats;
    private TableView<Product> adminProductTable;

    // Edit-product form fields
    private TextField txtEditName, txtEditDesc, txtEditPrice, txtEditDiscount, txtEditImg;
    private Label lblEditStatus;
    private int editingProductId = -1;

    public WarehouseController() {
        innerLayout = new VBox(22);
        innerLayout.setPadding(new Insets(28, 36, 40, 36));
        innerLayout.setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");

        buildUI();
        refreshWarehouseTable();

        viewLayout = new ScrollPane(innerLayout);
        viewLayout.setFitToWidth(true);
        innerLayout.setStyle("-fx-background-color: transparent; -fx-background: " + UIHelper.BG_LAVENDER + ";");
    }

    public ScrollPane getView() {
        return viewLayout;
    }

    private void buildUI() {
        // ── Header ───────────────────────────────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        VBox headerText = new VBox(4);
        Text title = UIHelper.pageTitle("Warehouses & Inventory");
        Text subtitle = UIHelper.pageSubtitle("View warehouse records, live stock levels, and manage products");
        headerText.getChildren().addAll(title, subtitle);
        headerRow.getChildren().add(headerText);

        innerLayout.getChildren().add(headerRow);

        lblStatus = new Label("");
        lblStatus.setWrapText(true);

        // ── Warehouse + Stock tables row ──────────────────────────────────────
        HBox tablesRow = new HBox(20);

        VBox whCard = UIHelper.shadowCard();
        HBox whHeaderRow = new HBox(10);
        whHeaderRow.setAlignment(Pos.CENTER_LEFT);
        Label lblWhTitle = UIHelper.cardTitle("🏭  Registered Warehouses");
        Label whHint = new Label("Click a row to view its stock");
        whHint.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");
        HBox.setHgrow(lblWhTitle, Priority.ALWAYS);
        whHeaderRow.getChildren().addAll(lblWhTitle, whHint);
        warehouseTable = new TableView<>();
        UIHelper.styleTable(warehouseTable, 220);
        setupWarehouseColumns();
        whCard.getChildren().addAll(whHeaderRow, warehouseTable);
        HBox.setHgrow(whCard, Priority.ALWAYS);

        VBox stockCard = UIHelper.shadowCard();
        Label lblStockTitle = UIHelper.cardTitle("📦  Live Stock Levels (Stored_in)");
        stockTable = new TableView<>();
        UIHelper.styleTable(stockTable, 220);
        setupStockColumns();
        stockCard.getChildren().addAll(lblStockTitle, stockTable);
        HBox.setHgrow(stockCard, Priority.ALWAYS);

        tablesRow.getChildren().addAll(whCard, stockCard);
        innerLayout.getChildren().add(tablesRow);

        warehouseTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                currentSelectedWarehouseId = nw.getWarehouseId();
                refreshStockTable(currentSelectedWarehouseId);
                lblStockTitle.setText("📦  Stock for: " + nw.getWarehouseName());
            }
        });

        innerLayout.getChildren().add(lblStatus);

        buildOperationsPanel();
        buildProductManagerPanel();
    }

    // ── Column setup ──────────────────────────────────────────────────────────

    private void setupWarehouseColumns() {
        TableColumn<Warehouse, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getWarehouseId()));
        colId.setPrefWidth(60);

        TableColumn<Warehouse, String> colName = new TableColumn<>("Warehouse Name");
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getWarehouseName()));
        colName.setPrefWidth(220);

        TableColumn<Warehouse, String> colLoc = new TableColumn<>("Location");
        colLoc.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocation()));
        colLoc.setPrefWidth(180);

        warehouseTable.getColumns().addAll(colId, colName, colLoc);
    }

    private void setupStockColumns() {
        TableColumn<String[], String> colProd = new TableColumn<>("Product");
        colProd.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue()[0]));
        colProd.setPrefWidth(300);

        TableColumn<String[], String> colQty = new TableColumn<>("Stock Qty");
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue()[1]));
        colQty.setPrefWidth(120);

        stockTable.getColumns().addAll(colProd, colQty);
    }

    private void setupAdminProductColumns() {
        TableColumn<Product, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getProductId()));
        colId.setPrefWidth(55);

        TableColumn<Product, String> colName = new TableColumn<>("Product Name");
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getProductName()));
        colName.setPrefWidth(200);

        TableColumn<Product, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getUnitPrice()));
        colPrice.setPrefWidth(90);

        TableColumn<Product, Double> colDiscount = new TableColumn<>("Disc %");
        colDiscount.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDiscount()));
        colDiscount.setPrefWidth(70);

        adminProductTable.getColumns().addAll(colId, colName, colPrice, colDiscount);
    }

    // ── Warehouse operations panel ────────────────────────────────────────────

    private void buildOperationsPanel() {
        VBox whOpsCard = UIHelper.plainCard();
        Label whOpsTitle = UIHelper.cardTitle("⚙️  Warehouse Operations");

        HBox addWhRow = new HBox(12);
        addWhRow.setAlignment(Pos.CENTER_LEFT);
        txtWHName = UIHelper.styledField("e.g. Ramallah Central");
        txtWHLocation = UIHelper.styledField("City");
        txtWHName.setPrefWidth(200);
        txtWHLocation.setPrefWidth(140);
        Button btnAddWH = UIHelper.primaryButton("Add Warehouse");
        btnAddWH.setOnAction(e -> handleAddWarehouseNode());
        addWhRow.getChildren().addAll(new Label("Name:"), txtWHName, new Label("Location:"), txtWHLocation, btnAddWH);

        HBox stockOpsRow = new HBox(12);
        stockOpsRow.setAlignment(Pos.CENTER_LEFT);
        stockOpsRow.setPadding(new Insets(6, 0, 0, 0));
        txtNewStockQty = UIHelper.styledField("New quantity");
        txtNewStockQty.setPrefWidth(130);
        Button btnUpdateStock = UIHelper.secondaryButton("Update Selected Product's Stock");
        btnUpdateStock.setOnAction(e -> handleStockLevelAdjustment());
        Label stockHint = new Label("Select a product row in the stock table first");
        stockHint.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");
        stockOpsRow.getChildren().addAll(new Label("New Stock Qty:"), txtNewStockQty, btnUpdateStock, stockHint);

        HBox reportsRow = new HBox(12);
        reportsRow.setAlignment(Pos.CENTER_LEFT);
        reportsRow.setPadding(new Insets(8, 0, 0, 0));
        Label reportLabel = new Label("Quick Reports:");
        reportLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_BODY + ";");

        Button btnOOS = UIHelper.secondaryButton("🚫  Out of Stock");
        btnOOS.setOnAction(e -> triggerPopup("Out of Stock Products", WarehouseDAO.getOutOfStockProducts(), "Warehouse: %s  |  Product: %s"));
        Button btnCats = UIHelper.secondaryButton("🗂️  Category Counts");
        btnCats.setOnAction(e -> triggerPopup("Products per Category", WarehouseDAO.getCategoryProductCounts(), "Category: %s  |  Products: %s"));
        Button btnLow = UIHelper.secondaryButton("📉  Lowest Stock");
        btnLow.setOnAction(e -> triggerPopup("10 Lowest Stock Products", WarehouseDAO.getLowestTenStocks(), "Product: %s  |  Total Stock: %s units"));

        reportsRow.getChildren().addAll(reportLabel, btnOOS, btnCats, btnLow);
        whOpsCard.getChildren().addAll(whOpsTitle, new Separator(), addWhRow, stockOpsRow, reportsRow);
        innerLayout.getChildren().add(whOpsCard);

        // ── Category manager (name + per-category warranty) + stock check ─────
        VBox catCard = UIHelper.plainCard();
        Label catTitle = UIHelper.cardTitle("🏷️  Category Manager");
        Label catSub = new Label("Each category has its own warranty duration. Click a row below to edit it.");
        catSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");

        // Add new category row
        HBox addCatRow = new HBox(12);
        addCatRow.setAlignment(Pos.CENTER_LEFT);
        TextField txtCatName = UIHelper.styledField("e.g. Tablets");
        TextField txtCatWarranty = UIHelper.styledField("Warranty (months)");
        txtCatName.setPrefWidth(180);
        txtCatWarranty.setPrefWidth(140);
        txtCatWarranty.setText("12");
        Button btnAddCat = UIHelper.primaryButton("Add Category");

        // Category table: shows existing categories with their warranty months
        TableView<Category> tblCategories = new TableView<>();
        UIHelper.styleTable(tblCategories, 180);
        TableColumn<Category, Integer> colCatId = new TableColumn<>("ID");
        colCatId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCategoryId()));
        colCatId.setPrefWidth(50);
        TableColumn<Category, String> colCatName = new TableColumn<>("Category Name");
        colCatName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCategoryName()));
        colCatName.setPrefWidth(200);
        TableColumn<Category, Integer> colCatWarranty = new TableColumn<>("Warranty (months)");
        colCatWarranty.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getWarrantyMonths()));
        colCatWarranty.setPrefWidth(140);
        tblCategories.getColumns().addAll(colCatId, colCatName, colCatWarranty);

        // Edit row: populated when a category row is selected
        HBox editCatRow = new HBox(12);
        editCatRow.setAlignment(Pos.CENTER_LEFT);
        editCatRow.setPadding(new Insets(6, 0, 0, 0));
        Label lblEditCatHint = new Label("Select a row above to edit its name or warranty.");
        lblEditCatHint.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");
        TextField txtEditCatName = UIHelper.styledField("New name");
        TextField txtEditCatWarranty = UIHelper.styledField("New warranty (months)");
        txtEditCatName.setPrefWidth(160);
        txtEditCatWarranty.setPrefWidth(150);
        Button btnSaveCat = UIHelper.successButton("💾  Save Category Changes");
        final int[] selectedCatId = {-1};

        Runnable refreshCategoryTable = () -> tblCategories.setItems(CategoryDAO.getAllCategories());
        refreshCategoryTable.run();

        tblCategories.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                selectedCatId[0] = sel.getCategoryId();
                txtEditCatName.setText(sel.getCategoryName());
                txtEditCatWarranty.setText(String.valueOf(sel.getWarrantyMonths()));
            }
        });

        btnSaveCat.setOnAction(e -> {
            if (selectedCatId[0] == -1) {
                showStatus("⚠️  Select a category row first.", "warn");
                return;
            }
            String newName = txtEditCatName.getText().trim();
            if (newName.isEmpty()) {
                showStatus("⚠️  Category name cannot be empty.", "warn");
                return;
            }
            int newWarranty;
            try {
                newWarranty = Integer.parseInt(txtEditCatWarranty.getText().trim());
                if (newWarranty < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showStatus("⚠️  Warranty months must be a non-negative integer.", "warn");
                return;
            }
            if (CategoryDAO.updateCategory(selectedCatId[0], newName, newWarranty)) {
                showStatus("✅  Category updated: " + newName + " — " + newWarranty + " month warranty.", "success");
                refreshCategoryTable.run();
                refreshCategoryDropdowns();
            } else {
                showStatus("❌  Update failed.", "error");
            }
        });

        editCatRow.getChildren().addAll(
                new Label("Rename to:"), txtEditCatName,
                new Label("Warranty:"), txtEditCatWarranty,
                btnSaveCat
        );

        btnAddCat.setOnAction(e -> {
            String name = txtCatName.getText().trim();
            if (name.isEmpty()) {
                showStatus("⚠️  Enter a category name first.", "warn");
                return;
            }
            int warrantyMonths;
            try {
                warrantyMonths = txtCatWarranty.getText().trim().isEmpty()
                        ? 12 : Integer.parseInt(txtCatWarranty.getText().trim());
                if (warrantyMonths < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showStatus("⚠️  Warranty months must be a non-negative integer.", "warn");
                return;
            }
            if (CategoryDAO.insertCategory(new Category(name, warrantyMonths))) {
                showStatus("✅  Category \"" + name + "\" added with a " + warrantyMonths + "-month warranty.", "success");
                txtCatName.clear();
                txtCatWarranty.setText("12");
                refreshCategoryDropdowns();
                refreshCategoryTable.run();
            } else {
                showStatus("❌  Failed — name may already exist.", "error");
            }
        });
        addCatRow.getChildren().addAll(
                new Label("New Category:"), txtCatName,
                new Label("Warranty (mo):"), txtCatWarranty,
                btnAddCat
        );

        HBox stockCheckRow = new HBox(12);
        stockCheckRow.setAlignment(Pos.CENTER_LEFT);
        stockCheckRow.setPadding(new Insets(6, 0, 0, 0));
        TextField txtWHId = UIHelper.styledField("WH ID");
        TextField txtProdId = UIHelper.styledField("Product ID");
        txtWHId.setPrefWidth(80);
        txtProdId.setPrefWidth(90);
        Button btnCheck = UIHelper.secondaryButton("Check Stock");
        Label lblStockResult = new Label("");
        lblStockResult.setStyle("-fx-font-weight: bold; -fx-text-fill: " + UIHelper.PURPLE_MAIN + "; -fx-font-size: 13px;");
        btnCheck.setOnAction(e -> {
            try {
                int qty = WarehouseDAO.getSpecificStockQuantity(
                        Integer.parseInt(txtWHId.getText().trim()),
                        Integer.parseInt(txtProdId.getText().trim())
                );
                lblStockResult.setText("Available: " + qty + " units");
            } catch (NumberFormatException ex) {
                lblStockResult.setText("⚠️  Invalid IDs");
            }
        });
        stockCheckRow.getChildren().addAll(new Label("Warehouse ID:"), txtWHId, new Label("Product ID:"), txtProdId, btnCheck, lblStockResult);

        catCard.getChildren().addAll(
                catTitle, catSub, addCatRow,
                new Separator(), tblCategories, lblEditCatHint, editCatRow,
                new Separator(), stockCheckRow
        );
        innerLayout.getChildren().add(catCard);
    }

    // ── Product manager panel (Browse + Edit + Delete) ────────────────────────

    private void buildProductManagerPanel() {
        VBox productCard = UIHelper.plainCard();
        Label productTitle = UIHelper.cardTitle("🔎  Browse, Edit & Delete Products by Category");
        Label productSub = new Label("Select a category to load its products, then click a row to populate the edit form.");
        productSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");

        // Category picker
        comboAdminCats = new ComboBox<>();
        comboAdminCats.setPromptText("Select Category");
        comboAdminCats.setPrefWidth(260);
        comboAdminCats.setStyle(
                "-fx-background-color: " + UIHelper.BG_LAVENDER + ";" +
                        "-fx-border-color: " + UIHelper.BORDER_LIGHT + ";" +
                        "-fx-border-radius: 10; -fx-background-radius: 10;" +
                        "-fx-padding: 6 10; -fx-font-size: 13px;"
        );
        comboAdminCats.setConverter(new javafx.util.StringConverter<Category>() {
            public String toString(Category c) { return c == null ? "" : c.getCategoryName() + "  (ID: " + c.getCategoryId() + ")"; }
            public Category fromString(String s) { return null; }
        });

        // Products table
        adminProductTable = new TableView<>();
        UIHelper.styleTable(adminProductTable, 200);
        setupAdminProductColumns();

        comboAdminCats.getSelectionModel().selectedItemProperty().addListener((obs, o, nw) -> {
            if (nw != null) refreshAdminProductTable(nw.getCategoryId());
            else adminProductTable.getItems().clear();
            clearEditForm();
        });

        // When a row is selected → populate the edit form
        adminProductTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) populateEditForm(selected);
        });

        HBox catPickerRow = new HBox(12);
        catPickerRow.setAlignment(Pos.CENTER_LEFT);
        catPickerRow.getChildren().addAll(new Label("Category:"), comboAdminCats);

        // ── Edit form ─────────────────────────────────────────────────────────
        VBox editSection = new VBox(12);
        editSection.setPadding(new Insets(14, 0, 0, 0));

        Label editTitle = UIHelper.cardTitle("✏️  Edit Selected Product");
        Label editHint = new Label("Click any product row above to load it here for editing.");
        editHint.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");

        // Two-column form grid
        javafx.scene.layout.GridPane editGrid = new javafx.scene.layout.GridPane();
        editGrid.setHgap(16);
        editGrid.setVgap(10);

        txtEditName     = UIHelper.styledField("Product Name");
        txtEditDesc     = UIHelper.styledField("Description");
        txtEditPrice    = UIHelper.styledField("Unit Price");
        txtEditDiscount = UIHelper.styledField("Discount %");
        txtEditImg      = UIHelper.styledField("Image filename");

        txtEditName.setPrefWidth(260);
        txtEditDesc.setPrefWidth(260);
        txtEditPrice.setPrefWidth(120);
        txtEditDiscount.setPrefWidth(120);
        txtEditImg.setPrefWidth(200);

        editGrid.add(new Label("Name *:"),     0, 0); editGrid.add(txtEditName,     1, 0);
        editGrid.add(new Label("Description:"),0, 1); editGrid.add(txtEditDesc,     1, 1);
        editGrid.add(new Label("Price ($) *:"),0, 2); editGrid.add(txtEditPrice,    1, 2);
        editGrid.add(new Label("Discount %:"), 0, 3); editGrid.add(txtEditDiscount, 1, 3);
        editGrid.add(new Label("Image file:"), 0, 4); editGrid.add(txtEditImg,      1, 4);

        // Action buttons
        lblEditStatus = new Label("");
        lblEditStatus.setWrapText(true);

        Button btnSave = UIHelper.primaryButton("💾  Save Changes");
        btnSave.setOnAction(e -> handleUpdateProduct());

        Button btnDelete = UIHelper.dangerButton("🗑️  Delete Product");
        btnDelete.setOnAction(e -> handleDeleteProduct());

        Button btnClearEdit = UIHelper.secondaryButton("✕  Clear");
        btnClearEdit.setOnAction(e -> clearEditForm());

        HBox editBtnRow = new HBox(12, btnSave, btnDelete, btnClearEdit);
        editBtnRow.setAlignment(Pos.CENTER_LEFT);

        editSection.getChildren().addAll(editTitle, editHint, editGrid, lblEditStatus, editBtnRow);

        productCard.getChildren().addAll(productTitle, productSub, catPickerRow, adminProductTable, new Separator(), editSection);
        innerLayout.getChildren().add(productCard);

        refreshCategoryDropdowns();
    }

    // ── Edit/Delete handlers ──────────────────────────────────────────────────

    private void populateEditForm(Product p) {
        editingProductId = p.getProductId();
        txtEditName.setText(p.getProductName());
        txtEditDesc.setText(p.getDescription() != null ? p.getDescription() : "");
        txtEditPrice.setText(String.valueOf(p.getUnitPrice()));
        txtEditDiscount.setText(String.valueOf(p.getDiscount()));
        txtEditImg.setText(p.getImageUrl() != null ? p.getImageUrl() : "");
        lblEditStatus.setText("");
    }

    private void clearEditForm() {
        editingProductId = -1;
        txtEditName.clear();
        txtEditDesc.clear();
        txtEditPrice.clear();
        txtEditDiscount.clear();
        txtEditImg.clear();
        lblEditStatus.setText("");
    }

    private void handleUpdateProduct() {
        if (editingProductId == -1) {
            showEditStatus("⚠️  No product selected. Click a row in the table first.", "warn");
            return;
        }
        String name = txtEditName.getText().trim();
        String desc = txtEditDesc.getText().trim();
        String priceStr = txtEditPrice.getText().trim();
        String discStr  = txtEditDiscount.getText().trim();
        String img      = txtEditImg.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            showEditStatus("⚠️  Name and Price are required.", "warn");
            return;
        }

        double price, discount;
        try {
            price    = Double.parseDouble(priceStr);
            discount = discStr.isEmpty() ? 0.0 : Double.parseDouble(discStr);
        } catch (NumberFormatException ex) {
            showEditStatus("⚠️  Price and Discount must be valid numbers.", "warn");
            return;
        }

        // Preserve the existing categoryId from the selected product
        Product selected = adminProductTable.getSelectionModel().getSelectedItem();
        int categoryId = selected != null ? selected.getCategoryId() : 0;

        Product updated = new Product(
                editingProductId, name, desc, price, discount,
                img.isEmpty() ? "fallback.png" : img,
                categoryId
        );

        if (ProductDAO.updateProduct(updated)) {
            showEditStatus("✅  Product \"" + name + "\" updated successfully.", "success");
            showStatus("✅  Product updated in the Products table.", "success");
            // Refresh the table for the current category
            if (comboAdminCats.getValue() != null)
                refreshAdminProductTable(comboAdminCats.getValue().getCategoryId());
        } else {
            showEditStatus("❌  Update failed. Check your database connection.", "error");
        }
    }

    private void handleDeleteProduct() {
        if (editingProductId == -1) {
            showEditStatus("⚠️  No product selected. Click a row in the table first.", "warn");
            return;
        }

        Product selected = adminProductTable.getSelectionModel().getSelectedItem();
        String name = selected != null ? selected.getProductName() : "this product";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete \"" + name + "\"?");
        confirm.setContentText(
                "This will permanently remove the product from the Products table.\n" +
                        "Associated Stored_in and Transaction_Items records may also be affected."
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (ProductDAO.deleteProduct(editingProductId)) {
                    showEditStatus("✅  Product deleted.", "success");
                    showStatus("✅  Product removed from the Products table.", "success");
                    clearEditForm();
                    if (comboAdminCats.getValue() != null)
                        refreshAdminProductTable(comboAdminCats.getValue().getCategoryId());
                } else {
                    showEditStatus("❌  Delete failed — the product may have linked Transaction_Items records.", "error");
                }
            }
        });
    }

    // ── Refresh helpers ───────────────────────────────────────────────────────

    private void refreshCategoryDropdowns() {
        try {
            ObservableList<Category> cats = CategoryDAO.getAllCategories();
            if (comboAdminCats != null) comboAdminCats.setItems(cats);
        } catch (Exception e) {
            System.err.println("[WarehouseController] Category refresh failed.");
        }
    }

    private void refreshAdminProductTable(int categoryId) {
        if (adminProductTable != null)
            adminProductTable.setItems(ProductDAO.getProductsByCategory(categoryId));
    }

    private void refreshWarehouseTable() {
        warehouseTable.setItems(WarehouseDAO.getAllWarehouses());
    }

    private void refreshStockTable(int warehouseId) {
        stockList = WarehouseDAO.getStockLevels(warehouseId);
        stockTable.setItems(stockList);
    }

    // ── Warehouse action handlers ─────────────────────────────────────────────

    private void handleAddWarehouseNode() {
        String name = txtWHName.getText().trim();
        String loc  = txtWHLocation.getText().trim();
        if (name.isEmpty() || loc.isEmpty()) {
            showStatus("⚠️  Enter both warehouse name and location.", "warn");
            return;
        }
        if (WarehouseDAO.insertWarehouse(name, loc)) {
            showStatus("✅  Warehouse \"" + name + "\" added in " + loc + ".", "success");
            refreshWarehouseTable();
            txtWHName.clear();
            txtWHLocation.clear();
        } else {
            showStatus("❌  Failed to add warehouse.", "error");
        }
    }

    private void handleStockLevelAdjustment() {
        String[] row = stockTable.getSelectionModel().getSelectedItem();
        if (row == null || currentSelectedWarehouseId == -1) {
            showStatus("⚠️  Select a product row in the stock table first.", "warn");
            return;
        }
        try {
            int prodId = Integer.parseInt(row[2]);
            int qty    = Integer.parseInt(txtNewStockQty.getText().trim());
            if (WarehouseDAO.updateStockQuantity(currentSelectedWarehouseId, prodId, qty)) {
                showStatus("✅  Stock updated to " + qty + " units.", "success");
                refreshStockTable(currentSelectedWarehouseId);
                txtNewStockQty.clear();
            } else {
                showStatus("❌  Stock update failed.", "error");
            }
        } catch (NumberFormatException ex) {
            showStatus("⚠️  Enter a valid integer for the stock quantity.", "warn");
        }
    }

    // ── Popup helper ──────────────────────────────────────────────────────────

    private void triggerPopup(String title, ObservableList<String[]> data, String format) {
        ListView<String> lv = new ListView<>();
        for (String[] row : data) lv.getItems().add(String.format(format, row[0], row[1]));
        javafx.stage.Stage popup = new javafx.stage.Stage();
        VBox layout = new VBox(lv);
        layout.setPadding(new Insets(16));
        layout.setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");
        popup.setScene(new javafx.scene.Scene(layout, 520, 340));
        popup.setTitle(title);
        popup.show();
    }

    // ── Status helpers ────────────────────────────────────────────────────────

    private void showStatus(String message, String type) {
        applyStatusStyle(lblStatus, message, type);
    }

    private void showEditStatus(String message, String type) {
        applyStatusStyle(lblEditStatus, message, type);
    }

    private void applyStatusStyle(Label lbl, String message, String type) {
        String color, bg;
        switch (type) {
            case "success": color = UIHelper.GREEN_SUCCESS; bg = UIHelper.GREEN_BG; break;
            case "error":   color = UIHelper.RED_ERROR;     bg = UIHelper.RED_BG;   break;
            default:        color = UIHelper.AMBER_WARN;    bg = UIHelper.AMBER_BG; break;
        }
        lbl.setText(message);
        lbl.setStyle(
                "-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;" +
                        "-fx-background-color: " + bg + "; -fx-padding: 8 14;" +
                        "-fx-background-radius: 8; -fx-wrap-text: true;"
        );
    }
}