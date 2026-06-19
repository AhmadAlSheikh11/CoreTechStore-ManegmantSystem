package com.example.dbsph4.gui;

import com.example.dbsph4.dao.TransactionDAO;
import com.example.dbsph4.database.SessionManager;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class TransactionController {

    private VBox viewLayout;
    private TableView<String[]> cartTable;
    private ObservableList<String[]> cartList;
    private Label lblTotal;
    private Label lblItemCount;
    private ComboBox<String> comboPayment;
    private Label lblStatus;

    public TransactionController() {
        viewLayout = new VBox(22);
        viewLayout.setPadding(new Insets(28, 36, 40, 36));
        viewLayout.setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");

        buildUI();
        refreshCartView();
    }

    public VBox getView() {
        return viewLayout;
    }

    private void buildUI() {
        // ── Header ───────────────────────────────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        VBox headerText = new VBox(4);
        Text title = UIHelper.pageTitle("Shopping Cart");
        Text subtitle = UIHelper.pageSubtitle("Review your selected items and complete your order");
        headerText.getChildren().addAll(title, subtitle);
        headerRow.getChildren().add(headerText);

        viewLayout.getChildren().add(headerRow);

        // ── Summary metric row ────────────────────────────────────────────────
        HBox summaryRow = new HBox(16);
        lblItemCount = new Label("0 items in cart");
        lblItemCount.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: " + UIHelper.TEXT_BODY + ";" +
                        "-fx-font-family: 'Segoe UI';"
        );
        summaryRow.setAlignment(Pos.CENTER_LEFT);
        summaryRow.getChildren().add(lblItemCount);
        viewLayout.getChildren().add(summaryRow);

        // ── Cart table card ───────────────────────────────────────────────────
        VBox tableCard = UIHelper.shadowCard();
        Label tableTitle = UIHelper.cardTitle("🛒  Cart Items");

        cartTable = new TableView<>();
        UIHelper.styleTable(cartTable, 300);
        setupCartTableColumns();

        tableCard.getChildren().addAll(tableTitle, cartTable);
        viewLayout.getChildren().add(tableCard);

        // ── Status feedback ───────────────────────────────────────────────────
        lblStatus = new Label("");
        lblStatus.setStyle("-fx-font-size: 13px; -fx-font-family: 'Segoe UI';");
        lblStatus.setWrapText(true);
        viewLayout.getChildren().add(lblStatus);

        // ── Checkout card ─────────────────────────────────────────────────────
        VBox checkoutCard = UIHelper.plainCard();

        // Top: total + payment
        HBox checkoutRow = new HBox(20);
        checkoutRow.setAlignment(Pos.CENTER_RIGHT);

        lblTotal = new Label("Total: $0.00");
        lblTotal.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: 800;" +
                        "-fx-text-fill: " + UIHelper.TEXT_DARK + "; -fx-font-family: 'Segoe UI';"
        );

        Label payLabel = new Label("Payment:");
        payLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + UIHelper.TEXT_BODY + ";");

        comboPayment = new ComboBox<>();
        comboPayment.getItems().addAll("Credit Card", "PayPal", "Cash on Delivery");
        comboPayment.setValue("Credit Card");
        comboPayment.setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: " + UIHelper.BORDER_LIGHT + ";" +
                        "-fx-border-radius: 10; -fx-background-radius: 10;" +
                        "-fx-padding: 6 10; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;"
        );

        Button btnCheckout = UIHelper.amazonGoldButton("✅  Place Order & Record Transaction");
        btnCheckout.setOnAction(e -> handleSecureCheckoutWorkflow());

        checkoutRow.getChildren().addAll(lblTotal, payLabel, comboPayment, btnCheckout);

        // Bottom: what happens note
        Label noteLabel = new Label(
                "ℹ️  Placing the order records a Transaction and Transaction_Items entry in the database, " +
                        "decrements Stored_in stock, and clears your cart — all inside a single ACID transaction."
        );
        noteLabel.setStyle(
                "-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_MUTED + ";" +
                        "-fx-wrap-text: true; -fx-font-family: 'Segoe UI';"
        );

        checkoutCard.getChildren().addAll(checkoutRow, noteLabel);
        viewLayout.getChildren().add(checkoutCard);
    }

    private void setupCartTableColumns() {
        TableColumn<String[], String> colName = new TableColumn<>("Product");
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colName.setPrefWidth(280);

        TableColumn<String[], String> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colQty.setPrefWidth(80);

        TableColumn<String[], String> colPrice = new TableColumn<>("Unit Price");
        colPrice.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty("$" + d.getValue()[2]));
        colPrice.setPrefWidth(120);

        TableColumn<String[], String> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty("$" + d.getValue()[3]));
        colSub.setPrefWidth(130);

        cartTable.getColumns().addAll(colName, colQty, colPrice, colSub);
    }

    private void refreshCartView() {
        if (!SessionManager.isCustomer()) return;
        int customerId = SessionManager.getCurrentCustomer().getCustomerId();

        cartList = TransactionDAO.getCartItems(customerId);
        cartTable.setItems(cartList);

        double total = 0.0;
        for (String[] row : cartList) total += Double.parseDouble(row[3]);
        lblTotal.setText(String.format("Total: $%.2f", total));
        lblItemCount.setText(cartList.size() + (cartList.size() == 1 ? " item" : " items") + " in cart");
    }

    private void handleSecureCheckoutWorkflow() {
        if (cartList == null || cartList.isEmpty()) {
            lblStatus.setStyle(
                    "-fx-text-fill: " + UIHelper.AMBER_WARN + "; -fx-font-weight: bold;" +
                            "-fx-background-color: " + UIHelper.AMBER_BG + "; -fx-padding: 8 14;" +
                            "-fx-background-radius: 8;"
            );
            lblStatus.setText("⚠️  Your cart is empty. Add some products before checking out.");
            return;
        }

        int customerId = SessionManager.getCurrentCustomer().getCustomerId();
        String method = comboPayment.getValue();
        double totalAmount = Double.parseDouble(
                lblTotal.getText().replace("Total: $", "")
        );

        if (TransactionDAO.executeACIDCheckout(customerId, totalAmount, method, cartList)) {
            lblStatus.setStyle(
                    "-fx-text-fill: " + UIHelper.GREEN_SUCCESS + "; -fx-font-weight: bold;" +
                            "-fx-background-color: " + UIHelper.GREEN_BG + "; -fx-padding: 8 14;" +
                            "-fx-background-radius: 8;"
            );
            lblStatus.setText(
                    "✅  Order placed! Transaction, items, and 1-year warranties recorded successfully."
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Confirmed");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Your order was placed successfully!\n\n" +
                            "Payment method: " + method + "\n" +
                            "Amount charged: $" + String.format("%.2f", totalAmount) + "\n\n" +
                            "A 1-year warranty has been automatically registered for every product in this order."
            );
            alert.showAndWait();

            refreshCartView();
        } else {
            lblStatus.setStyle(
                    "-fx-text-fill: " + UIHelper.RED_ERROR + "; -fx-font-weight: bold;" +
                            "-fx-background-color: " + UIHelper.RED_BG + "; -fx-padding: 8 14;" +
                            "-fx-background-radius: 8;"
            );
            lblStatus.setText("❌  Checkout failed: insufficient stock for one or more items.");
        }
    }
}