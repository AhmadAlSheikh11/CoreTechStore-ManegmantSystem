package com.example.dbsph4.gui;

import com.example.dbsph4.dao.TransactionDAO;
import com.example.dbsph4.dao.ProductDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class QueryScreen {

    private MainController controller;
    private ScrollPane viewLayout;

    public QueryScreen(MainController controller) {
        this.controller = controller;
        buildView();
    }

    private void buildView() {
        VBox pageContainer = new VBox(22);
        pageContainer.setPadding(new Insets(28, 36, 40, 36));
        pageContainer.setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");

        // ── Header ───────────────────────────────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        VBox headerText = new VBox(4);
        Text title = UIHelper.pageTitle("Transactions & Warranties");
        Text subtitle = UIHelper.pageSubtitle("Real-time revenue metrics, order auditing, and warranty tracking");
        headerText.getChildren().addAll(title, subtitle);
        headerRow.getChildren().add(headerText);

        pageContainer.getChildren().add(headerRow);

        // ── TabPane ──────────────────────────────────────────────────────────
        TabPane tabPane = new TabPane();
        tabPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-tab-header-background: transparent;"
        );
        tabPane.setTabMinHeight(36);

        Tab tabFinancial = new Tab("📈  Overview & Revenue");
        tabFinancial.setClosable(false);
        tabFinancial.setContent(buildFinancialTabContent());

        Tab tabAhmad = new Tab("🔍  Recent Order Lookup");
        tabAhmad.setClosable(false);
        tabAhmad.setContent(buildRecentOrderLookupTabContent());

        Tab tabProducts = new Tab("📦  Product Analysis");
        tabProducts.setClosable(false);
        tabProducts.setContent(buildProductIntelligenceTabContent());

        Tab tabCustomers = new Tab("👥  Customer Auditing");
        tabCustomers.setClosable(false);
        tabCustomers.setContent(buildCustomerAuditsTabContent());

        tabPane.getTabs().addAll(tabFinancial, tabAhmad, tabProducts, tabCustomers);
        pageContainer.getChildren().add(tabPane);

        viewLayout = new ScrollPane(pageContainer);
        viewLayout.setFitToWidth(true);
        viewLayout.setStyle("-fx-background-color: transparent; -fx-background: " + UIHelper.BG_LAVENDER + ";");
    }

    // ══ TAB 1: Financial Overview ═════════════════════════════════════════════

    private VBox buildFinancialTabContent() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20, 0, 0, 0));

        // ── Live metric cards with refresh button ─────────────────────────────
        // Stored in Labels so they can be updated without rebuilding the whole tab
        Label lblRevenue   = new Label("$0.00");
        Label lblWarranties = new Label("0");
        Label lblBasket    = new Label("0.00");

        lblRevenue.setStyle(
                "-fx-font-size: 26px; -fx-font-weight: 800;" +
                        "-fx-text-fill: " + UIHelper.PURPLE_MAIN + "; -fx-font-family: 'Segoe UI';"
        );
        lblWarranties.setStyle(
                "-fx-font-size: 26px; -fx-font-weight: 800;" +
                        "-fx-text-fill: " + UIHelper.GREEN_SUCCESS + "; -fx-font-family: 'Segoe UI';"
        );
        lblBasket.setStyle(
                "-fx-font-size: 26px; -fx-font-weight: 800;" +
                        "-fx-text-fill: " + UIHelper.AMBER_WARN + "; -fx-font-family: 'Segoe UI';"
        );

        // Helper: fetch live values and update all three labels at once
        Runnable refreshMetrics = () -> {
            double revenue    = TransactionDAO.getCurrentMonthRevenue();
            int    warranties = TransactionDAO.getActiveWarrantiesForLaptops();
            double basket     = TransactionDAO.getAverageItemsPerTransaction();
            lblRevenue.setText(String.format("$%.2f", revenue));
            lblWarranties.setText(String.valueOf(warranties));
            lblBasket.setText(String.format("%.2f units", basket));
        };

        // Initial load
        refreshMetrics.run();

        // Refresh button
        Button btnRefresh = UIHelper.secondaryButton("🔄  Refresh Metrics");
        btnRefresh.setOnAction(e -> refreshMetrics.run());

        // Build metric card shells with live labels injected
        VBox card1 = buildLiveMetricCard("💰", "Monthly Revenue",
                lblRevenue, "Total sales in the current fiscal period");
        VBox card2 = buildLiveMetricCard("🛡️", "Active Laptop Warranties (Q19)",
                lblWarranties, "Registered warranties for Laptops currently unexpired");
        VBox card3 = buildLiveMetricCard("🛒", "Avg Items / Transaction",
                lblBasket, "Average quantity of distinct items per checkout");

        HBox metricsRow = new HBox(16, card1, card2, card3);

        HBox refreshRow = new HBox();
        refreshRow.setAlignment(Pos.CENTER_RIGHT);
        refreshRow.getChildren().add(btnRefresh);

        layout.getChildren().addAll(refreshRow, metricsRow);

        // Tables row
        HBox tablesRow = new HBox(20);

        // Top Products
        VBox topCard = UIHelper.shadowCard();
        Label topTitle = UIHelper.cardTitle("🏆  Top 3 Products by Revenue");
        Label topSub = new Label("Best-performing products across all transactions");
        topSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");
        TableView<String[]> tblTop = new TableView<>();
        UIHelper.styleTable(tblTop, 180);
        setupTwoColumnTable(tblTop, "Product Name", "Gross Revenue");
        tblTop.setItems(TransactionDAO.getTopThreeRevenueProducts());
        topCard.getChildren().addAll(topTitle, topSub, tblTop);
        HBox.setHgrow(topCard, Priority.ALWAYS);

        // Expiring Warranties
        VBox expCard = UIHelper.shadowCard();
        Label expTitle = UIHelper.cardTitle("⏰  Warranties Expiring in 30 Days");
        Label expSub = new Label("Contact these customers before their coverage lapses");
        expSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.AMBER_WARN + ";");
        TableView<String[]> tblExp = new TableView<>();
        UIHelper.styleTable(tblExp, 180);
        setupWarrantyExpiryColumns(tblExp);
        tblExp.setItems(TransactionDAO.getWarrantiesExpiringIn30Days());
        expCard.getChildren().addAll(expTitle, expSub, tblExp);
        HBox.setHgrow(expCard, Priority.ALWAYS);

        tablesRow.getChildren().addAll(topCard, expCard);
        layout.getChildren().add(tablesRow);

        // High-value transactions
        VBox highCard = UIHelper.shadowCard();
        Label highTitle = UIHelper.cardTitle("💎  High-Value Transactions  —  Over $500");
        Label highSub = new Label("Sorted by total amount, highest first");
        highSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");
        TableView<String[]> tblHigh = new TableView<>();
        UIHelper.styleTable(tblHigh, 220);
        setupHighValueColumns(tblHigh);
        tblHigh.setItems(TransactionDAO.getHighValueTransactions());
        highCard.getChildren().addAll(highTitle, highSub, tblHigh);
        layout.getChildren().add(highCard);

        return layout;
    }

    // ══ TAB 2: Recent Order Lookup ═══════════════════════════════════════════

    private VBox buildRecentOrderLookupTabContent() {
        VBox layout = new VBox(16);
        layout.setPadding(new Insets(20, 0, 0, 0));

        // Search card
        VBox searchCard = UIHelper.plainCard();
        Label searchTitle = UIHelper.cardTitle("🔍  Customer Recent Order Lookup");
        Label searchSub = new Label(
                "Enter a customer email address to view the products, quantities, and warranty dates from their most recent order."
        );
        searchSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIHelper.TEXT_MUTED + "; -fx-wrap-text: true;");

        HBox inputRow = new HBox(12);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        TextField txtEmail = UIHelper.styledField("e.g. customer@email.com");
        txtEmail.setPrefWidth(280);
        Button btnLookup = UIHelper.primaryButton("Look Up Order");
        Label lblNoResult = new Label("");
        lblNoResult.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIHelper.AMBER_WARN + "; -fx-font-weight: bold;");
        inputRow.getChildren().addAll(new Label("Customer Email:"), txtEmail, btnLookup, lblNoResult);

        searchCard.getChildren().addAll(searchTitle, searchSub, inputRow);
        layout.getChildren().add(searchCard);

        // Results card
        VBox resultsCard = UIHelper.shadowCard();
        Label lblResultTitle = UIHelper.cardTitle("📋  Most Recent Order — Items & Warranties");

        TableView<String[]> tblOrder = new TableView<>();
        UIHelper.styleTable(tblOrder, 280);

        TableColumn<String[], String> colProd = new TableColumn<>("Product Name");
        colProd.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colProd.setPrefWidth(300);

        TableColumn<String[], String> colQty = new TableColumn<>("Quantity");
        colQty.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colQty.setPrefWidth(120);

        TableColumn<String[], String> colWar = new TableColumn<>("Warranty Expiry");
        colWar.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colWar.setPrefWidth(180);

        tblOrder.getColumns().addAll(colProd, colQty, colWar);
        resultsCard.getChildren().addAll(lblResultTitle, tblOrder);
        layout.getChildren().add(resultsCard);

        btnLookup.setOnAction(e -> {
            String email = txtEmail.getText().trim();
            if (email.isEmpty()) {
                lblNoResult.setText("⚠️  Enter a customer email address.");
                tblOrder.setItems(FXCollections.observableArrayList());
                return;
            }
            javafx.collections.ObservableList<String[]> results =
                    TransactionDAO.getRecentOrderDetailsByEmail(email);
            if (results.isEmpty()) {
                lblNoResult.setText("No orders found for: " + email);
            } else {
                lblNoResult.setText("");
                lblResultTitle.setText("📋  Most Recent Order for: " + email);
            }
            tblOrder.setItems(results);
        });

        return layout;
    }

    // ══ TAB 3: Product Analysis ═══════════════════════════════════════════════

    private VBox buildProductIntelligenceTabContent() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20, 0, 0, 0));

        VBox searchCard = UIHelper.plainCard();
        Label lblTitle = UIHelper.cardTitle("🔎  Product Revenue Lookup");
        Label lblSub = new Label("Enter a Product ID to see all transactions it appeared in and its total revenue.");
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");

        HBox inputRow = new HBox(12);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        TextField txtProductId = UIHelper.styledField("Product ID");
        txtProductId.setPrefWidth(130);
        Button btnAnalyze = UIHelper.primaryButton("Analyze");

        Label lblRevenueVal = new Label("Total Revenue: —");
        lblRevenueVal.setStyle(
                "-fx-font-weight: 800; -fx-font-size: 15px;" +
                        "-fx-text-fill: " + UIHelper.PURPLE_MAIN + "; -fx-font-family: 'Segoe UI';"
        );

        inputRow.getChildren().addAll(
                new Label("Product ID:"), txtProductId, btnAnalyze, lblRevenueVal
        );
        searchCard.getChildren().addAll(lblTitle, lblSub, inputRow);
        layout.getChildren().add(searchCard);

        VBox tableCard = UIHelper.shadowCard();
        Label lblTableTitle = UIHelper.cardTitle("📋  Transactions Containing This Product");

        TableView<String[]> tblTx = new TableView<>();
        UIHelper.styleTable(tblTx, 260);

        TableColumn<String[], String> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colCust.setPrefWidth(180);

        TableColumn<String[], String> colTx = new TableColumn<>("Transaction ID");
        colTx.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colTx.setPrefWidth(180);

        TableColumn<String[], String> colDate = new TableColumn<>("Transaction Date");
        colDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colDate.setPrefWidth(220);

        tblTx.getColumns().addAll(colCust, colTx, colDate);
        tableCard.getChildren().addAll(lblTableTitle, tblTx);
        layout.getChildren().add(tableCard);

        btnAnalyze.setOnAction(e -> {
            try {
                int prodId = Integer.parseInt(txtProductId.getText().trim());
                double rev = TransactionDAO.getTotalRevenueByProduct(prodId);
                lblRevenueVal.setText(String.format("Total Revenue: $%.2f", rev));
                tblTx.setItems(TransactionDAO.getTransactionsByProduct(prodId));
            } catch (NumberFormatException ex) {
                lblRevenueVal.setText("⚠️  Invalid ID");
                tblTx.setItems(FXCollections.observableArrayList());
            }
        });

        return layout;
    }

    // ══ TAB 4: Customer Auditing ═════════════════════════════════════════════

    private VBox buildCustomerAuditsTabContent() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20, 0, 0, 0));

        // ── Lookup card ───────────────────────────────────────────────────────
        VBox lookupCard = UIHelper.plainCard();
        Label lblAuditTitle = UIHelper.cardTitle("🔍  Customer Lookup");

        // ID search row
        HBox idRow = new HBox(12);
        idRow.setAlignment(Pos.CENTER_LEFT);
        TextField txtCustId = UIHelper.styledField("Customer ID");
        txtCustId.setPrefWidth(130);
        Button btnAudit = UIHelper.primaryButton("Audit Customer");
        idRow.getChildren().addAll(new Label("Customer ID:"), txtCustId, btnAudit);

        // Name search row
        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        nameRow.setPadding(new Insets(8, 0, 0, 0));
        TextField txtFirst = UIHelper.styledField("First Name");
        TextField txtLast = UIHelper.styledField("Last Name");
        txtFirst.setPrefWidth(120);
        txtLast.setPrefWidth(120);
        Button btnContact = UIHelper.secondaryButton("Get Contact Info");
        Label lblContact = new Label("");
        lblContact.setStyle(
                "-fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + ";" +
                        "-fx-font-size: 13px; -fx-font-family: 'Segoe UI';"
        );
        btnContact.setOnAction(ev -> {
            String f = txtFirst.getText().trim(), l = txtLast.getText().trim();
            if (!f.isEmpty() && !l.isEmpty()) {
                lblContact.setText(ProductDAO.getCustomerContactByName(f, l));
            } else {
                lblContact.setText("⚠️  Enter both names");
            }
        });
        nameRow.getChildren().addAll(new Label("Name search:"), txtFirst, txtLast, btnContact, lblContact);

        lookupCard.getChildren().addAll(lblAuditTitle, idRow, new Separator(), nameRow);
        layout.getChildren().add(lookupCard);

        // ── Results card ──────────────────────────────────────────────────────
        VBox resultsCard = UIHelper.shadowCard();
        Label lblResTitle = UIHelper.cardTitle("📋  Customer Profile & Active Warranties");

        Label lblItems = new Label("Items Purchased: —");
        lblItems.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + ";");

        Label lblRecent = new Label("Most Recent Order: —");
        lblRecent.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + ";");

        Label lblWarTitle = new Label("Active warranties (End Date > Today):");
        lblWarTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");

        TableView<String[]> tblWar = new TableView<>();
        UIHelper.styleTable(tblWar, 200);
        setupTwoColumnTable(tblWar, "Product", "Warranty Expiry Date");

        resultsCard.getChildren().addAll(lblResTitle, lblItems, lblRecent, lblWarTitle, tblWar);
        layout.getChildren().add(resultsCard);

        // ── Verify card ───────────────────────────────────────────────────────
        VBox verifyCard = UIHelper.plainCard();
        Label lblVerTitle = UIHelper.cardTitle("✅  Verify Transaction Item & Warranty");
        Label lblVerSub = new Label("Look up the sale price and warranty end date for a specific product in a transaction.");
        lblVerSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");

        HBox verifyRow = new HBox(12);
        verifyRow.setAlignment(Pos.CENTER_LEFT);
        TextField txtTxId = UIHelper.styledField("Transaction ID");
        TextField txtProdId = UIHelper.styledField("Product ID");
        txtTxId.setPrefWidth(120);
        txtProdId.setPrefWidth(120);
        Button btnVerify = UIHelper.amazonGoldButton("Verify");
        Label lblVerResult = new Label("");
        lblVerResult.setStyle(
                "-fx-font-weight: bold; -fx-text-fill: " + UIHelper.PURPLE_MAIN + ";" +
                        "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;"
        );
        verifyRow.getChildren().addAll(
                new Label("Transaction ID:"), txtTxId,
                new Label("Product ID:"), txtProdId,
                btnVerify, lblVerResult
        );
        verifyCard.getChildren().addAll(lblVerTitle, lblVerSub, verifyRow);
        layout.getChildren().add(verifyCard);

        // ── Handlers ──────────────────────────────────────────────────────────
        btnVerify.setOnAction(e -> {
            try {
                int cid = Integer.parseInt(txtCustId.getText().trim());
                int tid = Integer.parseInt(txtTxId.getText().trim());
                int pid = Integer.parseInt(txtProdId.getText().trim());
                lblVerResult.setText(TransactionDAO.getPriceAndWarrantyForProduct(cid, tid, pid));
            } catch (NumberFormatException ex) {
                lblVerResult.setText("⚠️  Enter valid IDs in all three fields");
            }
        });

        btnAudit.setOnAction(e -> {
            try {
                int cid = Integer.parseInt(txtCustId.getText().trim());
                int total = TransactionDAO.getTotalItemsPurchasedByCustomer(cid);
                lblItems.setText("Items Purchased: " + total + " units total");
                String recent = TransactionDAO.getMostRecentTransactionForCustomer(cid);
                lblRecent.setText("Most Recent Order: " + recent);
                tblWar.setItems(TransactionDAO.getCustomerActiveWarranties(cid));
            } catch (NumberFormatException ex) {
                lblItems.setText("Items Purchased: ⚠️  Invalid ID");
                lblRecent.setText("Most Recent Order: —");
                tblWar.setItems(FXCollections.observableArrayList());
            }
        });

        return layout;
    }

    // ── Live metric card builder ──────────────────────────────────────────────

    /**
     * Builds a metric card whose value label is injected from outside,
     * allowing the caller to update it on demand without rebuilding the card.
     */
    private VBox buildLiveMetricCard(String icon, String title, Label valueLabel, String description) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setPrefWidth(270);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 16px; -fx-border-radius: 16px;" +
                        "-fx-border-color: " + UIHelper.BORDER_LIGHT + "; -fx-border-width: 1px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.07), 14, 0, 0, 3);"
        );

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 18px;");
        Label titleLbl = new Label(title);
        titleLbl.setStyle(
                "-fx-font-size: 11px; -fx-font-weight: bold;" +
                        "-fx-text-fill: " + UIHelper.TEXT_MUTED + "; -fx-font-family: 'Segoe UI';"
        );
        titleRow.getChildren().addAll(iconLbl, titleLbl);

        Label descLbl = new Label(description);
        descLbl.setStyle(
                "-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";" +
                        "-fx-font-family: 'Segoe UI'; -fx-wrap-text: true;"
        );

        card.getChildren().addAll(titleRow, valueLabel, descLbl);
        return card;
    }

    // ── Column setup helpers ──────────────────────────────────────────────────

    private void setupTwoColumnTable(TableView<String[]> t, String h1, String h2) {
        TableColumn<String[], String> c1 = new TableColumn<>(h1);
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        c1.setPrefWidth(240);

        TableColumn<String[], String> c2 = new TableColumn<>(h2);
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        c2.setPrefWidth(160);

        t.getColumns().addAll(c1, c2);
    }

    private void setupWarrantyExpiryColumns(TableView<String[]> t) {
        TableColumn<String[], String> c1 = new TableColumn<>("Customer Name");
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        c1.setPrefWidth(150);

        TableColumn<String[], String> c2 = new TableColumn<>("Email");
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        c2.setPrefWidth(210);

        TableColumn<String[], String> c3 = new TableColumn<>("Phone");
        c3.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        c3.setPrefWidth(130);

        TableColumn<String[], String> c4 = new TableColumn<>("Expires");
        c4.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        c4.setPrefWidth(120);

        t.getColumns().addAll(c1, c2, c3, c4);
    }

    private void setupHighValueColumns(TableView<String[]> t) {
        TableColumn<String[], String> c1 = new TableColumn<>("Customer ID");
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        c1.setPrefWidth(140);

        TableColumn<String[], String> c2 = new TableColumn<>("Transaction ID");
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        c2.setPrefWidth(140);

        TableColumn<String[], String> c3 = new TableColumn<>("Total Amount");
        c3.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        c3.setPrefWidth(160);

        TableColumn<String[], String> c4 = new TableColumn<>("Date");
        c4.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        c4.setPrefWidth(160);

        t.getColumns().addAll(c1, c2, c3, c4);
    }

    public ScrollPane getView() {
        return viewLayout;
    }
}