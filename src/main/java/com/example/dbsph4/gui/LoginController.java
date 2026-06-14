package com.example.dbsph4.gui;

import com.example.dbsph4.database.DatabaseConnection;
import com.example.dbsph4.database.SessionManager;
import com.example.dbsph4.models.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.sql.*;

public class LoginController {

    private VBox viewLayout;
    private MainController mainController; // To refresh the main sidebar after login

    // UI Fields
    private TextField txtEmail;
    private PasswordField txtPassword;
    private TextField txtFirstName, txtLastName, txtPhone, txtAddress; // For Sign Up

    public LoginController(MainController mainController) {
        this.mainController = mainController;
        viewLayout = new VBox(15);
        viewLayout.setPadding(new Insets(30));
        viewLayout.setAlignment(Pos.CENTER);
        viewLayout.setStyle("-fx-background-color: #ffffff; -fx-max-width: 400px; -fx-border-color: #bdc3c7; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        showLoginView();
    }

    public VBox getView() {
        return viewLayout;
    }

    /**
     * Renders the Login Form
     */
    private void showLoginView() {
        viewLayout.getChildren().clear();

        Label titleLabel = new Label("Sign In to CoreTech");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        txtEmail = new TextField();
        txtEmail.setPromptText("Email Address");

        txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");

        Button btnLogin = new Button("Sign In");
        btnLogin.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        btnLogin.setOnAction(e -> handleLogin());

        Hyperlink linkSignUp = new Hyperlink("New to CoreTech? Create an account");
        linkSignUp.setOnAction(e -> showSignUpView());

        viewLayout.getChildren().addAll(titleLabel, txtEmail, txtPassword, btnLogin, linkSignUp);
    }

    /**
     * Renders the Sign Up Form
     */
    private void showSignUpView() {
        viewLayout.getChildren().clear();

        Label titleLabel = new Label("Create New Account");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        txtFirstName = new TextField(); txtFirstName.setPromptText("First Name *");
        txtLastName = new TextField();  txtLastName.setPromptText("Last Name *");
        txtEmail = new TextField();     txtEmail.setPromptText("Email (example@coretech.com) *");
        txtPassword = new PasswordField(); txtPassword.setPromptText("Password *");
        txtPhone = new TextField();     txtPhone.setPromptText("Phone Number");
        txtAddress = new TextField();   txtAddress.setPromptText("Delivery Address");

        Button btnRegister = new Button("Register Account");
        btnRegister.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        btnRegister.setOnAction(e -> handleSignUp());

        Hyperlink linkLogin = new Hyperlink("Already have an account? Sign In");
        linkLogin.setOnAction(e -> showLoginView());

        viewLayout.getChildren().addAll(titleLabel, txtFirstName, txtLastName, txtEmail, txtPassword, txtPhone, txtAddress, btnRegister, linkLogin);
    }

    /**
     * DATABASE VALIDATION via DAO (Clean Code Mode)
     */
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            System.err.println("[AUTH-WARN] Empty inputs blocked.");
            return;
        }

        // 1. Check Hardcoded Owner/Admin Credentials
        if (email.equalsIgnoreCase("admin@coretech.com") && password.equals("admin123")) {
            SessionManager.loginAsOwner();
            mainController.updateDynamicUIBasedOnRole();
            return;
        }

        // 2. Delegate database matching entirely to CustomerDAO
        Customer customer = com.example.dbsph4.dao.CustomerDAO.authenticateCustomer(email, password);

        if (customer != null) {
            SessionManager.loginAsCustomer(customer);
            mainController.updateDynamicUIBasedOnRole(); // Instant navigation switch
        } else {
            System.err.println("[AUTH-ERROR] Invalid Email or Password entered.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Credentials! Account not found.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    /**
     * DATABASE INSERT via DAO (Clean Code Mode)
     */
    private void handleSignUp() {
        String fName = txtFirstName.getText().trim();
        String lName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = txtPassword.getText();
        String phone = txtPhone.getText().trim();
        String addr = txtAddress.getText().trim();

        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            System.err.println("[AUTH-WARN] Mandatory fields check failed.");
            return;
        }

        Customer newCust = new Customer(fName, lName, email, pass,
                phone.isEmpty() ? null : phone,
                addr.isEmpty() ? null : addr);

        // Delegate execution to CustomerDAO
        if (com.example.dbsph4.dao.CustomerDAO.registerCustomer(newCust)) {
            System.out.println("[UI-SUCCESS] Interface synchronized with newly registered customer profile.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Account Created Successfully!", ButtonType.OK);
            alert.showAndWait();
            showLoginView(); // Back to sign-in screen automatically
        } else {
            System.err.println("[UI-ERROR] Failed to register account.");
        }
    }
}