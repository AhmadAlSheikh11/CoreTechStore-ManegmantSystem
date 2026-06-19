package com.example.dbsph4.gui;

import com.example.dbsph4.database.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainController {

    private BorderPane mainLayout;
    public StackPane contentArea;
    private VBox sidebar;
    private HBox topBar;
    private Label lblUserStatus;
    private Button btnAuthAction;

    public MainController() {
        mainLayout = new BorderPane();
        contentArea = new StackPane();
        sidebar = new VBox(8);
        topBar = new HBox(16);

        buildBaseLayout();
        updateDynamicUIBasedOnRole();
    }

    public BorderPane getView() {
        return mainLayout;
    }

    private void buildBaseLayout() {
        // ── Top Navigation Bar ───────────────────────────────────────────────
        topBar.setPadding(new Insets(14, 32, 14, 32));
        topBar.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: " + UIHelper.BORDER_LIGHT + ";" +
                        "-fx-border-width: 0 0 1.5px 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.06), 8, 0, 0, 2);"
        );
        topBar.setAlignment(Pos.CENTER);

        // Brand logo
        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        var logoStream = MainController.class.getResourceAsStream("/images/ui/logo.png");
        if (logoStream != null) {
            javafx.scene.image.Image logoImg = new javafx.scene.image.Image(logoStream);
            javafx.scene.image.ImageView logoIcon = new javafx.scene.image.ImageView(logoImg);
            logoIcon.setFitWidth(24);
            logoIcon.setFitHeight(24);
            logoIcon.setPreserveRatio(true);
            brandBox.getChildren().add(logoIcon);
        }

        Label lblCore = new Label("Core");
        lblCore.setStyle(
                "-fx-text-fill: " + UIHelper.TEXT_DARK + ";" +
                        "-fx-font-size: 19px; -fx-font-weight: 800; -fx-font-family: 'Segoe UI';"
        );

        Label lblTech = new Label("Tech");
        lblTech.setStyle(
                "-fx-text-fill: " + UIHelper.PURPLE_MAIN + ";" +
                        "-fx-font-size: 19px; -fx-font-weight: 800; -fx-font-family: 'Segoe UI';"
        );

        Label lblStore = new Label(" Store");
        lblStore.setStyle(
                "-fx-text-fill: " + UIHelper.TEXT_DARK + ";" +
                        "-fx-font-size: 19px; -fx-font-weight: 800; -fx-font-family: 'Segoe UI';"
        );

        HBox titleFlow = new HBox(0, lblCore, lblTech, lblStore);
        titleFlow.setAlignment(Pos.CENTER_LEFT);

        brandBox.getChildren().add(titleFlow);
        HBox.setHgrow(brandBox, Priority.ALWAYS);

        // Separator
        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep.setStyle("-fx-background-color: " + UIHelper.BORDER_LIGHT + ";");

        // User status label
        lblUserStatus = new Label("Guest Session");
        lblUserStatus.setStyle(
                "-fx-text-fill: " + UIHelper.TEXT_BODY + ";" +
                        "-fx-font-weight: 600; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';"
        );

        // Auth button
        btnAuthAction = new Button("Sign In");
        btnAuthAction.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: " + UIHelper.PURPLE_MAIN + ";" +
                        "-fx-border-color: " + UIHelper.PURPLE_MAIN + "; -fx-border-radius: 20px;" +
                        "-fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 7 18;" +
                        "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;" +
                        "-fx-background-radius: 20px;"
        );
        btnAuthAction.setOnAction(e -> handleAuthenticationState());
        btnAuthAction.setOnMouseEntered(ev -> btnAuthAction.setStyle(
                "-fx-background-color: " + UIHelper.PURPLE_SOFT + "; -fx-text-fill: " + UIHelper.PURPLE_DARK + ";" +
                        "-fx-border-color: " + UIHelper.PURPLE_DARK + "; -fx-border-radius: 20px;" +
                        "-fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 7 18;" +
                        "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-background-radius: 20px;"
        ));
        btnAuthAction.setOnMouseExited(ev -> btnAuthAction.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: " + UIHelper.PURPLE_MAIN + ";" +
                        "-fx-border-color: " + UIHelper.PURPLE_MAIN + "; -fx-border-radius: 20px;" +
                        "-fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 7 18;" +
                        "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-background-radius: 20px;"
        ));

        topBar.getChildren().addAll(brandBox, sep, lblUserStatus, btnAuthAction);

        // ── Sidebar ──────────────────────────────────────────────────────────
        sidebar.setPadding(new Insets(28, 14, 28, 14));
        sidebar.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: " + UIHelper.BORDER_LIGHT + ";" +
                        "-fx-border-width: 0 1.5px 0 0;"
        );
        sidebar.setPrefWidth(230);

        // ── Content Area ─────────────────────────────────────────────────────
        contentArea.setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");
        contentArea.setAlignment(Pos.CENTER);

        mainLayout.setTop(topBar);
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);
    }

    public void updateDynamicUIBasedOnRole() {
        sidebar.getChildren().clear();
        contentArea.getChildren().clear();

        // ── Public section ───────────────────────────────────────────────────
        Label menuHeader = UIHelper.sectionLabel("Marketplace");
        menuHeader.setPadding(new Insets(0, 0, 6, 8));
        sidebar.getChildren().add(menuHeader);

        Button btnBrowse = createSidebarNavButton("/images/ui/product.png", "Products & Categories");
        btnBrowse.setOnAction(e -> {
            var view = new ProductCatalogScreen(this).getView();
            if (view instanceof Region) {
                ((Region) view).setMaxWidth(Double.MAX_VALUE);
            }
            contentArea.getChildren().setAll(view);
        });
        sidebar.getChildren().add(btnBrowse);

        if (SessionManager.isGuest()) {
            lblUserStatus.setText("Browsing Anonymously");
            btnAuthAction.setText("Sign In");
            var view = new ProductCatalogScreen(this).getView();
            contentArea.getChildren().setAll(view);

        } else if (SessionManager.isCustomer()) {
            lblUserStatus.setText("👤  " + SessionManager.getCurrentCustomer().getFullName());
            btnAuthAction.setText("Sign Out");

            Button btnCart = createSidebarNavButton("/images/ui/cart.png", "My Cart");
            btnCart.setOnAction(e -> contentArea.getChildren().setAll(new TransactionController().getView()));
            sidebar.getChildren().add(btnCart);

            var view = new ProductCatalogScreen(this).getView();
            contentArea.getChildren().setAll(view);

        } else if (SessionManager.isOwner()) {
            lblUserStatus.setText("🔐  Administrator");
            btnAuthAction.setText("Sign Out");

            // Admin section divider
            Region spacer = new Region();
            spacer.setPrefHeight(10);
            Label adminHeader = UIHelper.sectionLabel("Management");
            adminHeader.setPadding(new Insets(4, 0, 6, 8));

            // Updated Admin Sidebar Buttons using your exact image names
            Button btnWarehouses = createSidebarNavButton("/images/ui/warehouses.png", "Warehouses & Stock");
            btnWarehouses.setOnAction(e -> contentArea.getChildren().setAll(new WarehouseController().getView()));

            Button btnBI = createSidebarNavButton("/images/ui/transaction.png", "Transactions & Warranties");
            btnBI.setOnAction(e -> contentArea.getChildren().setAll(new QueryScreen(this).getView()));

            Button btnAddProduct = createSidebarNavButton("/images/ui/add.png", "Add New Product");
            btnAddProduct.setOnAction(e -> contentArea.getChildren().setAll(new AddProductScreen(this).getView()));

            Button btnCustomers = createSidebarNavButton("/images/ui/customer.png", "Customer Registry");
            btnCustomers.setOnAction(e -> contentArea.getChildren().setAll(new CustomerListScreen().getView()));

            sidebar.getChildren().addAll(spacer, adminHeader, btnWarehouses, btnBI, btnAddProduct, btnCustomers);
            contentArea.getChildren().setAll(new QueryScreen(this).getView());
        }
    }

    Button createSidebarNavButton(String imagePath, String title) {
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        var stream = MainController.class.getResourceAsStream(imagePath);
        if (stream != null) {
            javafx.scene.image.Image img = new javafx.scene.image.Image(stream);
            javafx.scene.image.ImageView iconView = new javafx.scene.image.ImageView(img);
            iconView.setFitWidth(16);
            iconView.setFitHeight(16);
            iconView.setPreserveRatio(true);
            content.getChildren().add(iconView);
        } else {
            System.err.println("[FX-WARN] Resource not found: " + imagePath);
        }

        Label titleLbl = new Label(title);
        titleLbl.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: 600; -fx-font-family: 'Segoe UI';" +
                        "-fx-text-fill: " + UIHelper.TEXT_DARK + ";"
        );
        content.getChildren().add(titleLbl);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.setMaxWidth(Double.MAX_VALUE);
        String baseStyle =
                "-fx-background-color: transparent; -fx-alignment: center-left;" +
                        "-fx-padding: 10 14; -fx-background-radius: 10; -fx-cursor: hand;" +
                        "-fx-border-color: transparent; -fx-border-radius: 10;";
        String hoverStyle =
                "-fx-background-color: " + UIHelper.PURPLE_SOFT + "; -fx-alignment: center-left;" +
                        "-fx-padding: 10 14; -fx-background-radius: 10; -fx-cursor: hand;" +
                        "-fx-border-color: transparent; -fx-border-radius: 10;";

        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            titleLbl.setStyle(
                    "-fx-font-size: 13px; -fx-font-weight: 600; -fx-font-family: 'Segoe UI';" +
                            "-fx-text-fill: " + UIHelper.PURPLE_MAIN + ";"
            );
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            titleLbl.setStyle(
                    "-fx-font-size: 13px; -fx-font-weight: 600; -fx-font-family: 'Segoe UI';" +
                            "-fx-text-fill: " + UIHelper.TEXT_DARK + ";"
            );
        });
        return btn;
    }

    private void handleAuthenticationState() {
        if (!SessionManager.isGuest()) {
            SessionManager.logout();
            updateDynamicUIBasedOnRole();
        } else {
            contentArea.getChildren().setAll(new LoginView());
        }
    }

    // ═══ Login View ══════════════════════════════════════════════════════════

    public class LoginView extends VBox {
        private TextField txtEmail;
        private PasswordField txtPassword;
        private Label lblStatus;

        public LoginView() {
            super(18);
            setPadding(new Insets(40));
            setAlignment(Pos.CENTER);
            setMaxWidth(440);
            setStyle(
                    "-fx-background-color: " + UIHelper.CARD_WHITE + ";" +
                            "-fx-background-radius: 24px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.12), 20, 0, 0, 6);"
            );

            // Header
            Text title = UIHelper.pageTitle("Welcome Back");
            Text subtitle = UIHelper.pageSubtitle("Sign in to your CoreTech account");

            // Fields
            txtEmail = UIHelper.styledField("your@email.com");
            VBox emailGroup = UIHelper.fieldGroup("EMAIL ADDRESS", txtEmail);

            txtPassword = new PasswordField();
            txtPassword.setPromptText("••••••••");
            applyPasswordStyle(txtPassword, false);
            txtPassword.focusedProperty().addListener((o, w, f) -> applyPasswordStyle(txtPassword, f));
            VBox passwordGroup = UIHelper.fieldGroup("PASSWORD", txtPassword);

            lblStatus = new Label("");
            lblStatus.setWrapText(true);
            lblStatus.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI';");

            Button btnSignIn = UIHelper.amazonGoldButton("Sign In to Account");
            btnSignIn.setMaxWidth(Double.MAX_VALUE);
            btnSignIn.setOnAction(e -> handleLoginSecurityWorkflow());

            Separator sep = new Separator();

            Label lblPrompt = new Label("New to CoreTech?");
            lblPrompt.setStyle("-fx-text-fill: " + UIHelper.TEXT_MUTED + "; -fx-font-size: 12px;");

            Button btnGoRegister = new Button("Create an Account");
            btnGoRegister.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: " + UIHelper.PURPLE_MAIN + ";" +
                            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand; -fx-underline: true;"
            );
            btnGoRegister.setOnAction(e -> contentArea.getChildren().setAll(new RegisterView()));

            HBox switchRow = new HBox(6, lblPrompt, btnGoRegister);
            switchRow.setAlignment(Pos.CENTER);

            getChildren().addAll(title, subtitle, emailGroup, passwordGroup, lblStatus, btnSignIn, sep, switchRow);
        }

        private void applyPasswordStyle(PasswordField f, boolean focused) {
            f.setStyle(
                    "-fx-background-color: " + (focused ? "#ffffff" : UIHelper.BG_LAVENDER) + ";" +
                            "-fx-border-color: " + (focused ? UIHelper.BORDER_FOCUS : UIHelper.BORDER_LIGHT) + ";" +
                            "-fx-border-radius: 10; -fx-background-radius: 10;" +
                            "-fx-padding: 10 14 10 14; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';"
            );
        }

        private void handleLoginSecurityWorkflow() {
            String email = txtEmail.getText().trim();
            String pass = txtPassword.getText();

            if (email.isEmpty() || pass.isEmpty()) {
                lblStatus.setStyle("-fx-text-fill: " + UIHelper.RED_ERROR + "; -fx-font-weight: bold;");
                lblStatus.setText("⚠️  Please enter both your email and password.");
                return;
            }

            if (email.equals("admin@coretech.com") && pass.equals("admin123")) {
                SessionManager.loginAsOwner();
                updateDynamicUIBasedOnRole();
                return;
            }

            com.example.dbsph4.models.Customer customer =
                    com.example.dbsph4.dao.CustomerDAO.authenticateCustomer(email, pass);
            if (customer != null) {
                SessionManager.loginAsCustomer(customer);
                updateDynamicUIBasedOnRole();
            } else {
                lblStatus.setStyle("-fx-text-fill: " + UIHelper.RED_ERROR + "; -fx-font-weight: bold;");
                lblStatus.setText("❌  Invalid email or password. Please try again.");
            }
        }
    }

    // ═══ Register View ════════════════════════════════════════════════════════

    public class RegisterView extends StackPane {
        public RegisterView() {
            VBox pageWrapper = new VBox();
            pageWrapper.setAlignment(Pos.CENTER);
            pageWrapper.setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");
            pageWrapper.setPadding(new Insets(30, 0, 30, 0));
            VBox.setVgrow(pageWrapper, Priority.ALWAYS);

            VBox card = new VBox(14);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(36, 50, 36, 50));
            card.setMaxWidth(480);
            card.setStyle(
                    "-fx-background-color: #ffffff;" +
                            "-fx-background-radius: 24px; -fx-border-radius: 24px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.12), 24, 0.2, 0, 6);"
            );

            Text title = UIHelper.pageTitle("Create Account");
            Text sub = UIHelper.pageSubtitle("Register to start shopping on CoreTech");

            HBox nameRow = new HBox(12);
            TextField firstField = UIHelper.styledField("First name");
            TextField lastField = UIHelper.styledField("Last name");
            VBox firstBox = UIHelper.fieldGroup("FIRST NAME", firstField);
            VBox lastBox = UIHelper.fieldGroup("LAST NAME", lastField);
            HBox.setHgrow(firstBox, Priority.ALWAYS);
            HBox.setHgrow(lastBox, Priority.ALWAYS);
            nameRow.getChildren().addAll(firstBox, lastBox);

            TextField emailField = UIHelper.styledField("you@example.com");

            PasswordField passField = new PasswordField();
            passField.setPromptText("Choose a password");
            passField.setStyle(
                    "-fx-background-color: " + UIHelper.BG_LAVENDER + ";" +
                            "-fx-border-color: " + UIHelper.BORDER_LIGHT + "; -fx-border-radius: 10;" +
                            "-fx-background-radius: 10; -fx-padding: 10 14; -fx-font-size: 13px;"
            );
            VBox passBox = UIHelper.fieldGroup("PASSWORD", passField);

            TextField phoneField = UIHelper.styledField("059-000-0000");
            TextField addressField = UIHelper.styledField("City, Street Address");

            Label statusLbl = new Label("");
            statusLbl.setWrapText(true);
            statusLbl.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI';");

            Button registerBtn = UIHelper.amazonGoldButton("Create Account");
            registerBtn.setMaxWidth(Double.MAX_VALUE);

            registerBtn.setOnAction(e -> {
                String first = firstField.getText().trim();
                String last = lastField.getText().trim();
                String email = emailField.getText().trim();
                String password = passField.getText();

                if (first.isEmpty() || last.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    statusLbl.setStyle("-fx-text-fill: " + UIHelper.RED_ERROR + "; -fx-font-weight: bold;");
                    statusLbl.setText("⚠️  Please fill in all required fields.");
                    return;
                }
                if (!email.contains("@")) {
                    statusLbl.setStyle("-fx-text-fill: " + UIHelper.RED_ERROR + "; -fx-font-weight: bold;");
                    statusLbl.setText("⚠️  Please enter a valid email address.");
                    return;
                }

                com.example.dbsph4.models.Customer newCustomer = new com.example.dbsph4.models.Customer(
                        first, last, email, password,
                        phoneField.getText().trim(), addressField.getText().trim()
                );

                boolean success = com.example.dbsph4.dao.CustomerDAO.registerCustomer(newCustomer);
                if (success) {
                    statusLbl.setStyle("-fx-text-fill: " + UIHelper.GREEN_SUCCESS + "; -fx-font-weight: bold;");
                    statusLbl.setText("✅  Account created! You can now sign in.");
                } else {
                    statusLbl.setStyle("-fx-text-fill: " + UIHelper.RED_ERROR + "; -fx-font-weight: bold;");
                    statusLbl.setText("❌  Registration failed — that email may already be in use.");
                }
            });

            Separator sep = new Separator();

            Label loginPrompt = new Label("Already registered?");
            loginPrompt.setStyle("-fx-text-fill: " + UIHelper.TEXT_MUTED + "; -fx-font-size: 12px;");

            Button toLogin = new Button("Sign In");
            toLogin.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: " + UIHelper.PURPLE_MAIN + ";" +
                            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand; -fx-underline: true;"
            );
            toLogin.setOnAction(e -> contentArea.getChildren().setAll(new LoginView()));

            HBox switchRow = new HBox(6, loginPrompt, toLogin);
            switchRow.setAlignment(Pos.CENTER);

            card.getChildren().addAll(
                    title, sub, nameRow,
                    UIHelper.fieldGroup("EMAIL ADDRESS", emailField),
                    passBox,
                    UIHelper.fieldGroup("CONTACT PHONE", phoneField),
                    UIHelper.fieldGroup("SHIPPING ADDRESS", addressField),
                    statusLbl, registerBtn, sep, switchRow
            );

            pageWrapper.getChildren().add(card);
            getChildren().add(pageWrapper);
            setStyle("-fx-background-color: " + UIHelper.BG_LAVENDER + ";");
        }
    }
}