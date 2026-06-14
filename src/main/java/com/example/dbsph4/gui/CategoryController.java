package com.example.dbsph4.gui;

import com.example.dbsph4.dao.CategoryDAO;
import com.example.dbsph4.database.SessionManager;
import com.example.dbsph4.models.Category;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CategoryController {

    private VBox viewLayout;
    private TableView<Category> categoryTable;
    private TextField txtCategoryName;

    public CategoryController() {
        viewLayout = new VBox(15);
        viewLayout.setPadding(new Insets(20));
        buildUI();
        refreshTableData();
    }

    public VBox getView() {
        return viewLayout;
    }

    private void buildUI() {
        Label titleLabel = new Label("📂 CoreTech Store Categories");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        viewLayout.getChildren().add(titleLabel);

        // Setup JavaFX Table
        categoryTable = new TableView<>();

        TableColumn<Category, Integer> colId = new TableColumn<>("Category ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colId.setPrefWidth(120);

        TableColumn<Category, String> colName = new TableColumn<>("Category Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colName.setPrefWidth(250);

        categoryTable.getColumns().addAll(colId, colName);
        viewLayout.getChildren().add(categoryTable);

        // Conditional rendering: Show creation input ONLY for Store Owner
        if (SessionManager.isOwner()) {
            renderOwnerControls();
        }
    }

    private void renderOwnerControls() {
        Label lblForm = new Label("➕ Create New Category Node");
        lblForm.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        txtCategoryName = new TextField();
        txtCategoryName.setPromptText("Enter category name (e.g., Laptops, Monitors)");
        txtCategoryName.setPrefWidth(200);

        Button btnAdd = new Button("Register Category");
        btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        btnAdd.setOnAction(e -> handleInsert());

        HBox formBox = new HBox(10, txtCategoryName, btnAdd);
        viewLayout.getChildren().addAll(lblForm, formBox);
    }

    private void refreshTableData() {
        categoryTable.setItems(CategoryDAO.getAllCategories());
    }

    private void handleInsert() {
        String name = txtCategoryName.getText().trim();
        if (name.isEmpty()) {
            System.err.println("[UI-WARN] Empty category parameter blocked.");
            return;
        }

        Category cat = new Category(name);
        if (CategoryDAO.insertCategory(cat)) {
            refreshTableData();
            txtCategoryName.clear();
            System.out.println("[UI-SUCCESS] Interface synchronized with newly added category node.");
        }
    }
}