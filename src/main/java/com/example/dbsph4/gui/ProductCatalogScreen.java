package com.example.dbsph4.gui;

import com.example.dbsph4.dao.ProductDAO;
import com.example.dbsph4.dao.CategoryDAO;
import com.example.dbsph4.database.SessionManager;
import com.example.dbsph4.models.Product;
import com.example.dbsph4.models.Category;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ProductCatalogScreen {

    private ScrollPane viewLayout;
    private FlowPane productsGrid;
    private MainController controller;
    private TextField searchField;
    private int currentCategoryId = -1;
    private HBox categoriesRow;

    public ProductCatalogScreen(MainController controller) {
        this.controller = controller;

        VBox root = new VBox(20);
        root.setPadding(new Insets(24, 36, 40, 36));
        root.setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");
        root.setAlignment(Pos.TOP_CENTER); // Forces all child panels to align horizontally centered in the screen

        // ── Hero banner ───────────────────────────────────────────────────────
        StackPane heroBanner = buildHeroBanner();
        heroBanner.setMaxWidth(1100); // Prevents the banner from stretching infinitely on wide screens
        root.getChildren().add(heroBanner);

        // ── Search + filter bar ───────────────────────────────────────────────
        HBox searchBar = buildSearchAndFilterBar();
        searchBar.setMaxWidth(1100);
        root.getChildren().add(searchBar);

        // ── Category chips ────────────────────────────────────────────────────
        categoriesRow = new HBox(12);
        categoriesRow.setAlignment(Pos.CENTER); // Centered categories row layout
        categoriesRow.setPadding(new Insets(4, 0, 8, 0));
        categoriesRow.setMaxWidth(1100);
        refreshCategories();
        root.getChildren().add(categoriesRow);

        // ── Section header ────────────────────────────────────────────────────
        HBox sectionHeader = new HBox();
        sectionHeader.setAlignment(Pos.CENTER_LEFT); // Keeps text aligned left relative to its safe container row
        sectionHeader.setMaxWidth(1100);
        Label sectionTitle = new Label("All Products");
        sectionTitle.setStyle(
                "-fx-font-size: 17px; -fx-font-weight: bold;" +
                        "-fx-text-fill: " + UIHelper.TEXT_DARK + "; -fx-font-family: 'Segoe UI';"
        );
        sectionHeader.getChildren().add(sectionTitle);
        root.getChildren().add(sectionHeader);

        // ── Products grid ─────────────────────────────────────────────────────
        productsGrid = new FlowPane();
        productsGrid.setHgap(20);
        productsGrid.setVgap(20);
        productsGrid.setPadding(new Insets(4, 0, 4, 0));
        productsGrid.setAlignment(Pos.CENTER); // Centered products cards distribution grid row
        productsGrid.setMaxWidth(1100);
        root.getChildren().add(productsGrid);

        viewLayout = new ScrollPane(root);
        viewLayout.setFitToWidth(true);
        viewLayout.setStyle("-fx-background-color: transparent; -fx-background: " + UIHelper.BG_LAVENDER + ";");

        loadAllProducts();
    }

    public ScrollPane getView() {
        return viewLayout;
    }

    // ── Hero banner ───────────────────────────────────────────────────────────

    private StackPane buildHeroBanner() {
        StackPane banner = new StackPane();
        banner.setPrefHeight(160);
        banner.setMinHeight(160);

        ImageView bannerImg = new ImageView(UIHelper.loadImage("/images/ui/hero_banner.png"));
        bannerImg.setFitHeight(160);
        bannerImg.setPreserveRatio(false);
        bannerImg.fitWidthProperty().bind(banner.widthProperty());

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(30, 20, 70, 0.45);");

        VBox text = new VBox(6);
        text.setAlignment(Pos.CENTER_LEFT);
        text.setPadding(new Insets(0, 0, 0, 44));
        Label h1 = new Label("CoreTech Premium Store");
        h1.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 26px; -fx-font-weight: 800; -fx-font-family: 'Segoe UI';");
        Label h2 = new Label("High-end laptops, phones, and accessories");
        h2.setStyle("-fx-text-fill: #D4C8FA; -fx-font-size: 13px; -fx-font-weight: 600;");
        text.getChildren().addAll(h1, h2);

        banner.getChildren().addAll(bannerImg, overlay, text);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(24); clip.setArcHeight(24);
        clip.widthProperty().bind(banner.widthProperty());
        clip.heightProperty().bind(banner.heightProperty());
        banner.setClip(clip);

        return banner;
    }

    // ── Search & filter bar ───────────────────────────────────────────────────

    private HBox buildSearchAndFilterBar() {
        HBox bar = new HBox(14);
        bar.setAlignment(Pos.CENTER); // Centered inner layout row positioning for search and filter elements
        bar.setPadding(new Insets(4, 0, 4, 0));

        // Search box
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 10, 6, 18));
        searchBox.setPrefWidth(300);
        searchBox.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 30px;" +
                        "-fx-border-radius: 30px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.07), 10, 0, 0, 2);"
        );

        searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setStyle(
                "-fx-background-color: transparent; -fx-border-color: transparent;" +
                        "-fx-text-fill: " + UIHelper.TEXT_DARK + "; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';"
        );
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.setOnAction(e -> triggerSearch());

        Button searchBtn = new Button();
        searchBtn.setStyle(
                "-fx-background-color: " + UIHelper.PURPLE_MAIN + "; -fx-background-radius: 50%;" +
                        "-fx-min-width: 36px; -fx-min-height: 36px; -fx-max-width: 36px; -fx-max-height: 36px;" +
                        "-fx-cursor: hand;"
        );
        searchBtn.setGraphic(UIHelper.createSearchIcon(Color.WHITE));
        searchBtn.setOnAction(e -> triggerSearch());
        searchBox.getChildren().addAll(searchField, searchBtn);

        // Price filter box
        HBox priceBox = new HBox(8);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        priceBox.setPadding(new Insets(6, 14, 6, 14));
        priceBox.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 30px; -fx-border-radius: 30px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.07), 10, 0, 0, 2);"
        );

        Label minLabel = new Label("$");
        minLabel.setStyle("-fx-text-fill: " + UIHelper.TEXT_MUTED + "; -fx-font-weight: bold;");
        TextField txtMin = new TextField();
        txtMin.setPromptText("Min");
        txtMin.setPrefWidth(52);
        txtMin.setStyle(
                "-fx-background-color: transparent; -fx-border-color: transparent;" +
                        "-fx-font-size: 13px; -fx-text-fill: " + UIHelper.TEXT_DARK + ";"
        );

        Label maxLabel = new Label("– $");
        maxLabel.setStyle("-fx-text-fill: " + UIHelper.TEXT_MUTED + "; -fx-font-weight: bold;");
        TextField txtMax = new TextField();
        txtMax.setPromptText("Max");
        txtMax.setPrefWidth(52);
        txtMax.setStyle(
                "-fx-background-color: transparent; -fx-border-color: transparent;" +
                        "-fx-font-size: 13px; -fx-text-fill: " + UIHelper.TEXT_DARK + ";"
        );

        Button btnFilter = new Button("Filter Price");
        btnFilter.setStyle(
                "-fx-background-color: " + UIHelper.PURPLE_MAIN + "; -fx-text-fill: white;" +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 14;" +
                        "-fx-background-radius: 20; -fx-cursor: hand; -fx-font-family: 'Segoe UI';"
        );
        btnFilter.setOnAction(e -> {
            try {
                double min = txtMin.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtMin.getText().trim());
                double max = txtMax.getText().trim().isEmpty() ? 999999 : Double.parseDouble(txtMax.getText().trim());
                if (currentCategoryId == -1) loadAllProducts();
                else renderProducts(ProductDAO.getProductsByPriceRange(currentCategoryId, min, max));
            } catch (NumberFormatException ex) {
                Alert w = new Alert(Alert.AlertType.WARNING, "Enter valid numbers for min and max price.", ButtonType.OK);
                w.showAndWait();
            }
        });

        priceBox.getChildren().addAll(minLabel, txtMin, maxLabel, txtMax, btnFilter);
        bar.getChildren().addAll(searchBox, priceBox);
        return bar;
    }

    // ── Category chips ────────────────────────────────────────────────────────

    public void refreshCategories() {
        categoriesRow.getChildren().clear();
        categoriesRow.getChildren().add(buildCategoryChip("All", -1));
        try {
            for (Category cat : CategoryDAO.getAllCategories())
                categoriesRow.getChildren().add(buildCategoryChip(cat.getCategoryName(), cat.getCategoryId()));
        } catch (Exception e) {
            // fallback static chips
            categoriesRow.getChildren().addAll(
                    buildCategoryChip("Laptops", 1),
                    buildCategoryChip("Phones", 2),
                    buildCategoryChip("Accessories", 3),
                    buildCategoryChip("Monitors", 4)
            );
        }
    }

    private Button buildCategoryChip(String label, int categoryId) {
        Button chip = new Button(label);
        boolean isActive = (categoryId == currentCategoryId);
        chip.setStyle(
                "-fx-background-color: " + (isActive ? UIHelper.PURPLE_MAIN : "#ffffff") + ";" +
                        "-fx-text-fill: " + (isActive ? "white" : UIHelper.TEXT_DARK) + ";" +
                        "-fx-background-radius: 20px; -fx-border-radius: 20px;" +
                        "-fx-border-color: " + (isActive ? UIHelper.PURPLE_MAIN : UIHelper.BORDER_LIGHT) + ";" +
                        "-fx-border-width: 1.5px; -fx-padding: 7 18;" +
                        "-fx-font-weight: 600; -fx-font-size: 13px; -fx-cursor: hand; -fx-font-family: 'Segoe UI';"
        );
        chip.setOnAction(e -> {
            currentCategoryId = categoryId;
            refreshCategories();
            if (categoryId == -1) loadAllProducts();
            else renderProducts(ProductDAO.getProductsByPriceRange(categoryId, 0, 999999));
        });
        return chip;
    }

    // ── Product rendering ─────────────────────────────────────────────────────

    private void loadAllProducts() {
        renderProducts(ProductDAO.getAllProducts());
    }

    private void renderProducts(ObservableList<Product> products) {
        productsGrid.getChildren().clear();
        if (products == null || products.isEmpty()) {
            VBox empty = new VBox(10);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(60, 0, 60, 0));
            Label icon = new Label("🔍");
            icon.setStyle("-fx-font-size: 36px;");
            Label msg = new Label("No products match your criteria.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: " + UIHelper.TEXT_MUTED + "; -fx-font-style: italic;");
            empty.getChildren().addAll(icon, msg);
            productsGrid.getChildren().add(empty);
            return;
        }
        for (Product p : products) productsGrid.getChildren().add(buildProductCard(p));
    }

    private VBox buildProductCard(Product p) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setPrefWidth(220);
        card.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 20px; -fx-border-radius: 20px;" +
                        "-fx-border-color: " + UIHelper.BORDER_LIGHT + "; -fx-border-width: 1px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.05), 10, 0, 0, 3);" +
                        "-fx-cursor: hand;"
        );

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 20px; -fx-border-radius: 20px;" +
                        "-fx-border-color: " + UIHelper.PURPLE_MAIN + "; -fx-border-width: 1.5px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.14), 16, 0, 0, 5);" +
                        "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 20px; -fx-border-radius: 20px;" +
                        "-fx-border-color: " + UIHelper.BORDER_LIGHT + "; -fx-border-width: 1px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.05), 10, 0, 0, 3);" +
                        "-fx-cursor: hand;"
        ));

        // Product image
        AnchorPane imgFrame = new AnchorPane();
        imgFrame.setPrefHeight(130);
        imgFrame.setStyle(
                "-fx-background-color: " + UIHelper.BG_LAVENDER + ";" +
                        "-fx-background-radius: 14px;"
        );
        String imgPath = "/images/products/" + p.getImageUrl().toLowerCase().trim();
        ImageView pImg = new ImageView(UIHelper.loadImage(imgPath));
        pImg.setFitHeight(115);
        pImg.setFitWidth(185);
        pImg.setPreserveRatio(true);
        pImg.setLayoutX(5);
        pImg.setLayoutY(5);
        imgFrame.getChildren().add(pImg);

        // Discount badge
        if (p.getDiscount() > 0) {
            Label badge = new Label("-" + (int) p.getDiscount() + "%");
            badge.setStyle(
                    "-fx-background-color: #EF4444; -fx-text-fill: white;" +
                            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6;" +
                            "-fx-background-radius: 6px; -fx-font-family: 'Segoe UI';"
            );
            badge.setLayoutX(8);
            badge.setLayoutY(8);
            imgFrame.getChildren().add(badge);
        }

        // Product name
        Label nameLbl = new Label(p.getProductName());
        nameLbl.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-text-fill: " + UIHelper.TEXT_DARK + "; -fx-font-family: 'Segoe UI';"
        );
        nameLbl.setWrapText(true);

        // Price row
        HBox priceRow = new HBox();
        priceRow.setAlignment(Pos.CENTER_LEFT);

        double finalPrice = p.getUnitPrice() * (1 - p.getDiscount() / 100.0);
        Label priceLbl = new Label("$" + String.format("%.2f", finalPrice));
        priceLbl.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: 800;" +
                        "-fx-text-fill: " + UIHelper.TEXT_DARK + "; -fx-font-family: 'Segoe UI';"
        );

        if (p.getDiscount() > 0) {
            Label origPrice = new Label("$" + String.format("%.0f", p.getUnitPrice()));
            origPrice.setStyle(
                    "-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";" +
                            "-fx-strikethrough: true; -fx-padding: 0 0 0 6px;"
            );
            priceRow.getChildren().addAll(priceLbl, origPrice);
        } else {
            priceRow.getChildren().add(priceLbl);
        }

        HBox.setHgrow(priceRow, Priority.ALWAYS);

        // Add to cart button
        Button addBtn = new Button("🛒  Add");
        addBtn.setStyle(
                "-fx-background-color: " + UIHelper.AMAZON_GOLD + ";" +
                        "-fx-text-fill: " + UIHelper.AMAZON_DARK_TEXT + ";" +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;" +
                        "-fx-padding: 6 12; -fx-background-radius: 10px; -fx-font-family: 'Segoe UI';"
        );
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(
                "-fx-background-color: " + UIHelper.AMAZON_GOLD_HOVER + ";" +
                        "-fx-text-fill: " + UIHelper.AMAZON_DARK_TEXT + ";" +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;" +
                        "-fx-padding: 6 12; -fx-background-radius: 10px; -fx-font-family: 'Segoe UI';"
        ));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(
                "-fx-background-color: " + UIHelper.AMAZON_GOLD + ";" +
                        "-fx-text-fill: " + UIHelper.AMAZON_DARK_TEXT + ";" +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;" +
                        "-fx-padding: 6 12; -fx-background-radius: 10px; -fx-font-family: 'Segoe UI';"
        ));
        addBtn.setOnAction(e -> handleAddToCart(p.getProductId()));

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(priceRow, Priority.ALWAYS);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        footer.getChildren().addAll(priceRow, spacer, addBtn);

        card.getChildren().addAll(imgFrame, nameLbl, footer);
        return card;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void triggerSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) loadAllProducts();
        else renderProducts(ProductDAO.searchProductsByName(q));
    }

    private void handleAddToCart(int productId) {
        if (!SessionManager.isCustomer()) {
            controller.contentArea.getChildren().setAll(controller.new LoginView());
        } else {
            executeCartInsert(productId);
        }
    }

    private void executeCartInsert(int productId) {
        int custId = SessionManager.getCurrentCustomer().getCustomerId();
        String sql = "INSERT INTO Adds_to_Cart (CustomerID, ProductID, SelectedQuantity) " +
                "VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE SelectedQuantity = SelectedQuantity + 1";
        System.out.println("[SQL-DAO] Adding product " + productId + " to cart for customer " + custId);
        try (java.sql.Connection conn = com.example.dbsph4.database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("[SQL-DAO-ERROR] executeCartInsert: could not get DB connection.");
                return;
            }
            ps.setInt(1, custId);
            ps.setInt(2, productId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("[SQL-DAO] Adds_to_Cart updated for customer " + custId);
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Added to Cart");
                a.setHeaderText(null);
                a.setContentText("Product added to your cart!");
                a.showAndWait();
            } else {
                System.err.println("[SQL-DAO-ERROR] executeCartInsert: INSERT/UPDATE returned 0 rows.");
            }
        } catch (java.sql.SQLException ex) {
            System.err.println("[SQL-DAO-ERROR] executeCartInsert failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}