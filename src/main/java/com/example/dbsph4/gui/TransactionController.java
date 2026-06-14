package com.example.dbsph4.gui;

import com.example.dbsph4.dao.TransactionDAO;
import com.example.dbsph4.database.SessionManager;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TransactionController {

    private VBox viewLayout;
    private TableView<String[]> cartTable;
    private ObservableList<String[]> cartList;
    private Label lblTotal;
    private ComboBox<String> comboPayment;

    public TransactionController() {
        viewLayout = new VBox(15);
        viewLayout.setPadding(new Insets(20));

        buildUI();
        refreshCartView();
    }

    public VBox getView() {
        return viewLayout;
    }

    private void buildUI() {
        Label titleLabel = new Label("🛍️ My Active Shopping Cart");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        viewLayout.getChildren().add(titleLabel);

        cartTable = new TableView<>();
        setupCartTableColumns();
        viewLayout.getChildren().add(cartTable);

        lblTotal = new Label("Order Total: $0.00");
        lblTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        comboPayment = new ComboBox<>();
        comboPayment.getItems().addAll("Credit Card", "PayPal", "Cash on Delivery");
        comboPayment.setValue("Credit Card");

        Button btnCheckout = new Button("🚀 Confirm & Place Order (ACID)");
        btnCheckout.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCheckout.setOnAction(e -> handleCheckout());

        HBox checkoutBox = new HBox(20, lblTotal, comboPayment, btnCheckout);
        viewLayout.getChildren().add(checkoutBox);
    }

    private void setupCartTableColumns() {
        TableColumn<String[], String> colName = new TableColumn<>("Product");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        colName.setPrefWidth(180);

        TableColumn<String[], String> colQty = new TableColumn<>("Quantity");
        colQty.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        colQty.setPrefWidth(80);

        TableColumn<String[], String> colPrice = new TableColumn<>("Unit Price ($)");
        colPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        colPrice.setPrefWidth(100);

        TableColumn<String[], String> colTotal = new TableColumn<>("Total ($)");
        colTotal.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        colTotal.setPrefWidth(120);

        cartTable.getColumns().addAll(colName, colQty, colPrice, colTotal);
    }

    private void refreshCartView() {
        if (!SessionManager.isCustomer()) return;
        int customerId = SessionManager.getCurrentCustomer().getCustomerId();

        cartList = TransactionDAO.getCartItems(customerId); // Direct Clean DAO Fetch
        cartTable.setItems(cartList);

        double total = 0.0;
        for (String[] row : cartList) {
            total += Double.parseDouble(row[3]);
        }
        lblTotal.setText(String.format("Order Total: $%.2f", total));
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) return;

        int customerId = SessionManager.getCurrentCustomer().getCustomerId();
        String method = comboPayment.getValue();
        double totalAmount = Double.parseDouble(lblTotal.getText().replace("Order Total: $", ""));

        // Direct handoff to ACID DAO pipeline handler
        if (TransactionDAO.executeACIDCheckout(customerId, totalAmount, method, cartList)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order placed successfully! Committed.", ButtonType.OK);
            alert.showAndWait();
            refreshCartView(); // Wipe and sync UI controls
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Checkout Failed: Inventory shortfalls or error.", ButtonType.OK);
            alert.showAndWait();
        }
    }
}