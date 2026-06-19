package com.example.dbsph4.gui;

import com.example.dbsph4.dao.CustomerDAO;
import com.example.dbsph4.models.Customer;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class CustomerListScreen {

    private ScrollPane viewLayout;

    public CustomerListScreen() {
        buildView();
    }

    public ScrollPane getView() {
        return viewLayout;
    }

    private void buildView() {
        VBox pageContainer = new VBox(22);
        pageContainer.setPadding(new Insets(28, 36, 40, 36));
        pageContainer.setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");

        // ── Header ────────────────────────────────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        VBox headerText = new VBox(4);
        Text title    = UIHelper.pageTitle("Customer Registry");
        Text subtitle = UIHelper.pageSubtitle("All registered customer accounts loaded from the Customers table");
        headerText.getChildren().addAll(title, subtitle);
        headerRow.getChildren().add(headerText);

        pageContainer.getChildren().add(headerRow);

        // ── Summary metric ────────────────────────────────────────────────────
        ObservableList<Customer> customers = CustomerDAO.getAllCustomers();

        HBox metricsRow = new HBox(16);
        metricsRow.getChildren().add(
                UIHelper.metricCard("👥", "Total Registered Customers",
                        String.valueOf(customers.size()),
                        "All accounts in the Customers table", UIHelper.PURPLE_MAIN)
        );
        pageContainer.getChildren().add(metricsRow);

        // ── Customer table card ───────────────────────────────────────────────
        VBox tableCard = UIHelper.shadowCard();
        Label tableTitle = UIHelper.cardTitle("📋  All Customers — Customers Table");
        Label tableSub   = new Label("Fetched via CustomerDAO.getAllCustomers()  ·  Read-only view");
        tableSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");

        TableView<Customer> table = new TableView<>();
        UIHelper.styleTable(table, 480);
        setupCustomerColumns(table);
        table.setItems(customers);

        // Refresh button
        Button btnRefresh = UIHelper.secondaryButton("🔄  Refresh");
        btnRefresh.setOnAction(e -> {
            ObservableList<Customer> fresh = CustomerDAO.getAllCustomers();
            table.setItems(fresh);
            tableTitle.setText("📋  All Customers — " + fresh.size() + " records");
        });

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(tableTitle, Priority.ALWAYS);
        topRow.getChildren().addAll(tableTitle, btnRefresh);

        tableCard.getChildren().addAll(topRow, tableSub, table);
        pageContainer.getChildren().add(tableCard);

        // ── Detail panel — shows selected customer's full record ──────────────
        VBox detailCard = UIHelper.plainCard();
        Label detailTitle = UIHelper.cardTitle("🔍  Selected Customer Detail");
        Label detailHint  = new Label("Click any row above to inspect the full record.");
        detailHint.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");

        // Six labelled fields displayed inline
        Label lblId      = detailField("Customer ID", "—");
        Label lblName    = detailField("Full Name",   "—");
        Label lblEmail   = detailField("Email",       "—");
        Label lblPhone   = detailField("Phone",       "—");
        Label lblAddress = detailField("Address",     "—");

        javafx.scene.layout.GridPane detailGrid = new javafx.scene.layout.GridPane();
        detailGrid.setHgap(24);
        detailGrid.setVgap(10);
        detailGrid.add(detailLabel("Customer ID:"), 0, 0); detailGrid.add(lblId,      1, 0);
        detailGrid.add(detailLabel("Full Name:"),   0, 1); detailGrid.add(lblName,    1, 1);
        detailGrid.add(detailLabel("Email:"),       0, 2); detailGrid.add(lblEmail,   1, 2);
        detailGrid.add(detailLabel("Phone:"),       2, 0); detailGrid.add(lblPhone,   3, 0);
        detailGrid.add(detailLabel("Address:"),     2, 1); detailGrid.add(lblAddress, 3, 1);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                lblId.setText(String.valueOf(selected.getCustomerId()));
                lblName.setText(selected.getFullName());
                lblEmail.setText(selected.getEmail());
                lblPhone.setText(selected.getPhone() != null ? selected.getPhone() : "—");
                lblAddress.setText(selected.getAddress() != null ? selected.getAddress() : "—");
            } else {
                lblId.setText("—"); lblName.setText("—"); lblEmail.setText("—");
                lblPhone.setText("—"); lblAddress.setText("—");
            }
        });

        detailCard.getChildren().addAll(detailTitle, detailHint, detailGrid);
        pageContainer.getChildren().add(detailCard);

        viewLayout = new ScrollPane(pageContainer);
        viewLayout.setFitToWidth(true);
        viewLayout.setStyle("-fx-background-color: transparent; -fx-background: " + UIHelper.BG_LAVENDER + ";");
    }

    // ── Column setup ──────────────────────────────────────────────────────────

    private void setupCustomerColumns(TableView<Customer> table) {
        TableColumn<Customer, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCustomerId()));
        colId.setPrefWidth(55);

        TableColumn<Customer, String> colFirst = new TableColumn<>("First Name");
        colFirst.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFirstName()));
        colFirst.setPrefWidth(130);

        TableColumn<Customer, String> colLast = new TableColumn<>("Last Name");
        colLast.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLastName()));
        colLast.setPrefWidth(130);

        TableColumn<Customer, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colEmail.setPrefWidth(220);

        TableColumn<Customer, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getPhone() != null ? c.getValue().getPhone() : "—"
        ));
        colPhone.setPrefWidth(130);

        TableColumn<Customer, String> colAddress = new TableColumn<>("Address");
        colAddress.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getAddress() != null ? c.getValue().getAddress() : "—"
        ));
        colAddress.setPrefWidth(200);

        table.getColumns().addAll(colId, colFirst, colLast, colEmail, colPhone, colAddress);
    }

    // ── Small helpers ─────────────────────────────────────────────────────────

    private Label detailLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";");
        return l;
    }

    private Label detailField(String fieldName, String initialValue) {
        Label l = new Label(initialValue);
        l.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + ";");
        return l;
    }
}