package com.example.dbsph4.gui;

import com.example.dbsph4.dao.CategoryDAO;
import com.example.dbsph4.dao.ProductDAO;
import com.example.dbsph4.dao.WarehouseDAO;
import com.example.dbsph4.models.Category;
import com.example.dbsph4.models.Product;
import com.example.dbsph4.models.Warehouse;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class AddProductScreen {

    private ScrollPane viewLayout;
    private MainController controller;

    private ComboBox<Category> comboCategory;
    private ComboBox<Warehouse> comboWarehouse;
    private TextField txtName, txtDescription, txtPrice, txtDiscount, txtImageUrl, txtStock;
    private Label lblStatus;
    private TableView<String[]> recentProductsTable;

    public AddProductScreen(MainController controller) {
        this.controller = controller;
        buildView();
    }

    public ScrollPane getView() {
        return viewLayout;
    }

    private void buildView() {
        VBox pageContainer = new VBox(24);
        pageContainer.setPadding(new Insets(28, 36, 40, 36));
        pageContainer.setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");

        // ── Page Header ──────────────────────────────────────────────────────
        Text title    = UIHelper.pageTitle("Add New Product");
        Text subtitle = UIHelper.pageSubtitle("Register a product in a category and assign it initial warehouse stock");
        pageContainer.getChildren().addAll(title, subtitle);

        // ── Two-column layout ────────────────────────────────────────────────
        HBox columns = new HBox(24);
        columns.setAlignment(Pos.TOP_LEFT);

        // ── LEFT: Product Form Card ──────────────────────────────────────────
        VBox formCard = UIHelper.shadowCard();
        formCard.setPrefWidth(480);
        HBox.setHgrow(formCard, Priority.ALWAYS);

        Label formTitle = UIHelper.cardTitle("📦  Product Details");
        Label formSub = new Label("Fill in the product information below. Fields marked * are required.");
        formSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIHelper.TEXT_MUTED + "; -fx-wrap-text: true;");

        // Category picker
        comboCategory = new ComboBox<>();
        comboCategory.setPromptText("— Select a Category —");
        comboCategory.setMaxWidth(Double.MAX_VALUE);
        styleCombo(comboCategory);
        comboCategory.setConverter(new javafx.util.StringConverter<Category>() {
            public String toString(Category c) { return c == null ? "" : c.getCategoryName(); }
            public Category fromString(String s) { return null; }
        });
        loadCategories();
        VBox categoryGroup = UIHelper.fieldGroup("CATEGORY  *", comboCategory);

        // Category info (live product count for selected category)
        Label lblCategoryInfo = new Label("");
        lblCategoryInfo.setStyle(
                "-fx-font-size: 12px; -fx-text-fill: " + UIHelper.PURPLE_MAIN + ";" +
                        "-fx-font-weight: bold; -fx-font-family: 'Segoe UI';"
        );
        comboCategory.setOnAction(e -> {
            Category sel = comboCategory.getValue();
            if (sel != null) {
                int count = ProductDAO.getProductsByCategory(sel.getCategoryId()).size();
                lblCategoryInfo.setText(
                        sel.getCategoryName() + " — " + count + " products currently in this category" +
                                "  |  Warranty: " + sel.getWarrantyMonths() + " month(s) (set in Warehouses > Category Manager)"
                );
            }
        });

        // Product fields
        txtName = UIHelper.styledField("e.g. Dell XPS 15 Laptop");
        VBox nameGroup = UIHelper.fieldGroup("PRODUCT NAME  *", txtName);

        txtDescription = UIHelper.styledField("Short description of the product...");
        VBox descGroup = UIHelper.fieldGroup("DESCRIPTION", txtDescription);

        txtPrice    = UIHelper.styledField("e.g. 1299.99");
        txtDiscount = UIHelper.styledField("e.g. 10.00");
        VBox priceGroup    = UIHelper.fieldGroup("UNIT PRICE ($)  *", txtPrice);
        VBox discountGroup = UIHelper.fieldGroup("DISCOUNT (%)", txtDiscount);
        HBox priceRow = new HBox(16, priceGroup, discountGroup);
        HBox.setHgrow(priceGroup, Priority.ALWAYS);
        HBox.setHgrow(discountGroup, Priority.ALWAYS);

        txtImageUrl = UIHelper.styledField("e.g. laptop_dell.png");
        VBox imageGroup = UIHelper.fieldGroup("IMAGE FILENAME", txtImageUrl);

        // Warehouse picker
        comboWarehouse = new ComboBox<>();
        comboWarehouse.setPromptText("— Select Warehouse —");
        comboWarehouse.setMaxWidth(Double.MAX_VALUE);
        styleCombo(comboWarehouse);
        comboWarehouse.setConverter(new javafx.util.StringConverter<Warehouse>() {
            public String toString(Warehouse w) {
                return w == null ? "" : w.getWarehouseName() + "  ·  " + w.getLocation();
            }
            public Warehouse fromString(String s) { return null; }
        });
        loadWarehouses();
        VBox warehouseGroup = UIHelper.fieldGroup("INITIAL WAREHOUSE  *", comboWarehouse);

        txtStock = UIHelper.styledField("e.g. 50");
        txtStock.setText("100");
        VBox stockGroup = UIHelper.fieldGroup("INITIAL STOCK QTY  *", txtStock);
        HBox stockRow = new HBox(16, warehouseGroup, stockGroup);
        HBox.setHgrow(warehouseGroup, Priority.ALWAYS);
        HBox.setHgrow(stockGroup, Priority.ALWAYS);

        // Status feedback
        lblStatus = new Label("");
        lblStatus.setWrapText(true);
        lblStatus.setStyle("-fx-font-size: 12px;");
        lblStatus.setVisible(false);

        // Action buttons
        Button btnSubmit = UIHelper.amazonGoldButton("✅  Add Product");
        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setOnAction(e -> handleProductSubmit());

        Button btnClear = UIHelper.secondaryButton("Clear Form");
        btnClear.setOnAction(e -> clearForm());
        btnClear.setMaxWidth(Double.MAX_VALUE);

        HBox actionRow = new HBox(12, btnSubmit, btnClear);
        actionRow.setAlignment(Pos.CENTER);
        HBox.setHgrow(btnSubmit, Priority.ALWAYS);

        formCard.getChildren().addAll(
                formTitle, formSub,
                new Separator(),
                categoryGroup, lblCategoryInfo,
                nameGroup, descGroup,
                priceRow, imageGroup, stockRow,
                new Separator(),
                lblStatus, actionRow
        );

        // ── RIGHT: Quick guide + Recently added ──────────────────────────────
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(340);

        // Quick guide card
        VBox tipsCard = UIHelper.plainCard();
        Label tipsTitle = UIHelper.cardTitle("💡  Quick Guide");
        VBox tipsList = new VBox(10);
        tipsList.getChildren().addAll(
                tipRow("1", "Select a category",    "Choose where this product will appear in the catalog."),
                tipRow("2", "Enter product details", "Name and price are required. Discount defaults to 0% if left blank."),
                tipRow("3", "Set image filename",    "Place the image in /images/products/. Leave blank to use the fallback."),
                tipRow("4", "Pick a warehouse",      "This warehouse receives the initial stock. All others are set to 0."),
                tipRow("5", "Submit",                "The product is saved and inventory is updated automatically.")
        );
        tipsCard.getChildren().addAll(tipsTitle, tipsList);

        // Recently added products card
        VBox recentCard = UIHelper.plainCard();
        Label recentTitle = UIHelper.cardTitle("🕐  Recently Added Products");
        recentProductsTable = new TableView<>();
        UIHelper.styleTable(recentProductsTable, 200);

        TableColumn<String[], String> colName = new TableColumn<>("Product Name");
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colName.setPrefWidth(160);

        TableColumn<String[], String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colCat.setPrefWidth(100);

        TableColumn<String[], String> colPrice2 = new TableColumn<>("Price");
        colPrice2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colPrice2.setPrefWidth(80);

        recentProductsTable.getColumns().addAll(colName, colCat, colPrice2);
        refreshRecentProducts();
        recentCard.getChildren().addAll(recentTitle, recentProductsTable);

        rightCol.getChildren().addAll(tipsCard, recentCard);

        columns.getChildren().addAll(formCard, rightCol);
        pageContainer.getChildren().add(columns);

        viewLayout = new ScrollPane(pageContainer);
        viewLayout.setFitToWidth(true);
        viewLayout.setStyle("-fx-background-color: transparent; -fx-background: " + UIHelper.BG_LAVENDER + ";");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HBox tipRow(String num, String heading, String body) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.TOP_LEFT);

        Label numLbl = new Label(num);
        numLbl.setStyle(
                "-fx-min-width: 22px; -fx-min-height: 22px; -fx-max-width: 22px; -fx-max-height: 22px;" +
                        "-fx-background-color: " + UIHelper.PURPLE_SOFT + ";" +
                        "-fx-background-radius: 50%; -fx-text-fill: " + UIHelper.PURPLE_MAIN + ";" +
                        "-fx-font-size: 11px; -fx-font-weight: bold; -fx-alignment: center;"
        );

        VBox text = new VBox(2);
        Label h = new Label(heading);
        h.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + ";");
        Label b = new Label(body);
        b.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + "; -fx-wrap-text: true;");
        b.setMaxWidth(260);
        text.getChildren().addAll(h, b);
        row.getChildren().addAll(numLbl, text);
        return row;
    }

    private void styleCombo(ComboBox<?> combo) {
        combo.setStyle(
                "-fx-background-color: " + UIHelper.BG_LAVENDER + ";" +
                        "-fx-border-color: " + UIHelper.BORDER_LIGHT + ";" +
                        "-fx-border-radius: 10; -fx-background-radius: 10;" +
                        "-fx-padding: 4 8; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';"
        );
    }

    private void loadCategories() {
        try { comboCategory.setItems(CategoryDAO.getAllCategories()); }
        catch (Exception e) { System.err.println("[AddProductScreen] Failed to load categories."); }
    }

    private void loadWarehouses() {
        try { comboWarehouse.setItems(WarehouseDAO.getAllWarehouses()); }
        catch (Exception e) { System.err.println("[AddProductScreen] Failed to load warehouses."); }
    }

    private void refreshRecentProducts() {
        javafx.collections.ObservableList<String[]> rows =
                javafx.collections.FXCollections.observableArrayList();
        ObservableList<Product> all  = ProductDAO.getAllProducts();
        ObservableList<Category> cats = CategoryDAO.getAllCategories();
        java.util.Map<Integer, String> catMap = new java.util.HashMap<>();
        for (Category c : cats) catMap.put(c.getCategoryId(), c.getCategoryName());

        int start = Math.max(0, all.size() - 10);
        for (int i = all.size() - 1; i >= start; i--) {
            Product p = all.get(i);
            rows.add(new String[]{
                    p.getProductName(),
                    catMap.getOrDefault(p.getCategoryId(), "—"),
                    String.format("$%.2f", p.getUnitPrice())
            });
        }
        recentProductsTable.setItems(rows);
    }

    // ── Form submission ───────────────────────────────────────────────────────

    private void handleProductSubmit() {
        Category selectedCat = comboCategory.getValue();
        Warehouse selectedWH = comboWarehouse.getValue();
        String name        = txtName.getText().trim();
        String desc        = txtDescription.getText().trim();
        String priceStr    = txtPrice.getText().trim();
        String discountStr = txtDiscount.getText().trim();
        String imgUrl      = txtImageUrl.getText().trim();
        String stockStr    = txtStock.getText().trim();

        if (selectedCat == null) { showStatus("⚠️  Please select a category.", "warn"); return; }
        if (name.isEmpty())      { showStatus("⚠️  Product name is required.", "warn"); return; }
        if (priceStr.isEmpty())  { showStatus("⚠️  Unit price is required.", "warn"); return; }
        if (selectedWH == null)  { showStatus("⚠️  Please select a warehouse.", "warn"); return; }
        if (stockStr.isEmpty())  { showStatus("⚠️  Initial stock quantity is required.", "warn"); return; }

        double price, discount;
        int stock;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showStatus("⚠️  Price must be a positive number (e.g. 199.99).", "warn"); return;
        }
        try {
            discount = discountStr.isEmpty() ? 0.0 : Double.parseDouble(discountStr);
            if (discount < 0 || discount > 100) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showStatus("⚠️  Discount must be between 0 and 100.", "warn"); return;
        }
        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showStatus("⚠️  Stock quantity must be a non-negative integer.", "warn"); return;
        }

        Product newProduct = new Product(
                0, name, desc, price, discount,
                imgUrl.isEmpty() ? "fallback.png" : imgUrl,
                selectedCat.getCategoryId()
        );

        if (ProductDAO.insertProduct(newProduct, selectedWH.getWarehouseId(), stock)) {
            showStatus(
                    "✅  \"" + name + "\" added to " + selectedCat.getCategoryName() +
                            " with " + stock + " units in " + selectedWH.getWarehouseName() + ".",
                    "success"
            );
            refreshRecentProducts();
            clearForm();
        } else {
            showStatus("❌  Insertion failed. Check your connection or data constraints.", "error");
        }
    }

    private void clearForm() {
        comboCategory.setValue(null);
        comboWarehouse.setValue(null);
        txtName.clear(); txtDescription.clear();
        txtPrice.clear(); txtDiscount.clear();
        txtImageUrl.clear();
        txtStock.setText("100");
        lblStatus.setVisible(false);
    }

    private void showStatus(String message, String type) {
        String color, bg;
        switch (type) {
            case "success": color = UIHelper.GREEN_SUCCESS; bg = UIHelper.GREEN_BG; break;
            case "error":   color = UIHelper.RED_ERROR;     bg = UIHelper.RED_BG;   break;
            default:        color = UIHelper.AMBER_WARN;    bg = UIHelper.AMBER_BG; break;
        }
        lblStatus.setText(message);
        lblStatus.setStyle(
                "-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;" +
                        "-fx-background-color: " + bg + "; -fx-padding: 8 14; -fx-background-radius: 8;" +
                        "-fx-wrap-text: true;"
        );
        lblStatus.setVisible(true);
    }
}