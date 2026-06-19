package com.example.dbsph4.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Circle;
import java.io.InputStream;
import java.sql.Connection;

public class UIHelper {

    // ─── Color Palette ────────────────────────────────────────────────────────
    public static final String BG_LAVENDER   = "#F1EEFB";
    public static final String BG_SECTION    = "#F8F6FE";
    public static final String CARD_WHITE    = "#FFFFFF";
    public static final String PURPLE_MAIN   = "#7C5CFC";
    public static final String PURPLE_DARK   = "#5B3FD9";
    public static final String PURPLE_SOFT   = "#EDE7FF";
    public static final String PURPLE_GLOW   = "rgba(124,92,252,0.12)";
    public static final String TEXT_DARK     = "#2D2A3D";
    public static final String TEXT_BODY     = "#4A4660";
    public static final String TEXT_MUTED    = "#8B86A0";
    public static final String BORDER_LIGHT  = "#E3DEF8";
    public static final String BORDER_FOCUS  = "#7C5CFC";
    public static final String GREEN_SUCCESS = "#16A34A";
    public static final String GREEN_BG      = "#F0FDF4";
    public static final String RED_ERROR     = "#DC2626";
    public static final String RED_BG        = "#FEF2F2";
    public static final String AMBER_WARN    = "#D97706";
    public static final String AMBER_BG      = "#FFFBEB";

    // Amazon-inspired UI tokens
    public static final String AMAZON_GOLD        = "#FFD814";
    public static final String AMAZON_GOLD_HOVER  = "#F7CA00";
    public static final String AMAZON_GOLD_BORDER = "#FCD200";
    public static final String AMAZON_DARK        = "#131921";
    public static final String AMAZON_LINK_BLUE   = "#007185";
    public static final String AMAZON_DARK_TEXT   = "#0F1111";

    // ─── DB Status Indicator ─────────────────────────────────────────────────

    /**
     * Builds a live database connection status badge.
     * Shows green dot + "DB Connected" or red dot + "DB Offline".
     */
    public static HBox buildDbStatusBadge() {
        HBox badge = new HBox(6);
        badge.setAlignment(Pos.CENTER);
        badge.setPadding(new Insets(5, 12, 5, 12));

        boolean connected = checkDbConnection();

        Circle dot = new Circle(5);
        dot.setFill(connected ? Color.web(GREEN_SUCCESS) : Color.web(RED_ERROR));

        Label lblStatus = new Label(connected ? "DB Connected" : "DB Offline");
        lblStatus.setStyle(
                "-fx-font-size: 11px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                        "-fx-text-fill: " + (connected ? GREEN_SUCCESS : RED_ERROR) + ";"
        );

        String bgColor = connected ? GREEN_BG : RED_BG;
        String borderColor = connected ? "#BBF7D0" : "#FECACA";
        badge.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-radius: 20px;" +
                        "-fx-background-radius: 20px;"
        );

        badge.getChildren().addAll(dot, lblStatus);
        return badge;
    }

    /** Pings the database to check liveness. */
    private static boolean checkDbConnection() {
        try (Connection conn = com.example.dbsph4.database.DatabaseConnection.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    // ─── Metric Cards ─────────────────────────────────────────────────────────

    /**
     * Analytics metric card with icon accent, large value, and description.
     */
    public static VBox metricCard(String icon, String title, String value, String description, String accentColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setPrefWidth(270);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-radius: 16px;" +
                        "-fx-border-color: " + BORDER_LIGHT + ";" +
                        "-fx-border-width: 1px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.07), 14, 0, 0, 3);"
        );

        // Icon + title row
        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 18px;");
        Label titleLbl = new Label(title);
        titleLbl.setStyle(
                "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-family: 'Segoe UI'; -fx-text-transform: uppercase;"
        );
        titleRow.getChildren().addAll(iconLbl, titleLbl);

        Label valueLbl = new Label(value);
        valueLbl.setStyle(
                "-fx-font-size: 26px; -fx-font-weight: 800;" +
                        "-fx-text-fill: " + accentColor + ";" +
                        "-fx-font-family: 'Segoe UI';"
        );

        Label descLbl = new Label(description);
        descLbl.setStyle(
                "-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-family: 'Segoe UI'; -fx-wrap-text: true;"
        );

        // Bottom accent bar
        javafx.scene.layout.Region accentBar = new javafx.scene.layout.Region();
        accentBar.setPrefHeight(3);
        accentBar.setMaxWidth(40);
        accentBar.setStyle("-fx-background-color: " + accentColor + "; -fx-background-radius: 2px;");

        card.getChildren().addAll(titleRow, valueLbl, descLbl, accentBar);
        return card;
    }

    // ─── Section Headers ──────────────────────────────────────────────────────

    /** Bold page title in dark brand color. */
    public static Text pageTitle(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 26));
        t.setFill(Color.web(TEXT_DARK));
        return t;
    }

    /** Lighter subtitle below page titles. */
    public static Text pageSubtitle(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("Segoe UI", 13));
        t.setFill(Color.web(TEXT_MUTED));
        return t;
    }

    /** Small uppercase section eyebrow label. */
    public static Label sectionLabel(String text) {
        Label lbl = new Label(text.toUpperCase());
        lbl.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 1.2;" +
                        "-fx-text-fill: " + TEXT_MUTED + "; -fx-font-family: 'Segoe UI';"
        );
        return lbl;
    }

    /** Card section heading. */
    public static Label cardTitle(String text) {
        Label lbl = new Label(text);
        lbl.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_DARK + "; -fx-font-family: 'Segoe UI';"
        );
        return lbl;
    }

    // ─── Form Fields ──────────────────────────────────────────────────────────

    /** Styled text input with focus ring. */
    public static TextField styledField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        applyFieldStyle(field, false);
        field.focusedProperty().addListener((obs, was, isFocused) -> applyFieldStyle(field, isFocused));
        return field;
    }

    private static void applyFieldStyle(TextField field, boolean focused) {
        field.setStyle(
                "-fx-background-color: " + (focused ? "#ffffff" : BG_LAVENDER) + ";" +
                        "-fx-border-color: " + (focused ? BORDER_FOCUS : BORDER_LIGHT) + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + TEXT_DARK + ";" +
                        "-fx-font-family: 'Segoe UI';"
        );
    }

    /** Labelled field group (eyebrow label + input). */
    public static VBox fieldGroup(String labelText, javafx.scene.Node field) {
        VBox box = new VBox(5);
        Label lbl = new Label(labelText);
        lbl.setStyle(
                "-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 10px;" +
                        "-fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                        "-fx-letter-spacing: 0.8;"
        );
        box.getChildren().addAll(lbl, field);
        return box;
    }

    // ─── Buttons ──────────────────────────────────────────────────────────────

    public static Button primaryButton(String label) {
        Button btn = new Button(label);
        String base = baseButtonStyle(PURPLE_MAIN, "white");
        String hover = baseButtonStyle(PURPLE_DARK, "white");
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    public static Button secondaryButton(String label) {
        Button btn = new Button(label);
        String base =
                "-fx-background-color: white; -fx-text-fill: " + PURPLE_MAIN + ";" +
                        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                        "-fx-padding: 9 20 9 20; -fx-background-radius: 12;" +
                        "-fx-border-color: " + PURPLE_MAIN + "; -fx-border-radius: 12; -fx-cursor: hand;";
        String hover =
                "-fx-background-color: " + PURPLE_SOFT + "; -fx-text-fill: " + PURPLE_DARK + ";" +
                        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                        "-fx-padding: 9 20 9 20; -fx-background-radius: 12;" +
                        "-fx-border-color: " + PURPLE_DARK + "; -fx-border-radius: 12; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    /** Danger/destructive action button. */
    public static Button dangerButton(String label) {
        Button btn = new Button(label);
        String base = baseButtonStyle(RED_ERROR, "white");
        String hover = baseButtonStyle("#B91C1C", "white");
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    /** Success / confirm action button. */
    public static Button successButton(String label) {
        Button btn = new Button(label);
        String base = baseButtonStyle(GREEN_SUCCESS, "white");
        String hover = baseButtonStyle("#15803D", "white");
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    /** Amazon-gold checkout button. */
    public static Button amazonGoldButton(String label) {
        Button btn = new Button(label);
        String base =
                "-fx-background-color: " + AMAZON_GOLD + "; -fx-text-fill: " + AMAZON_DARK_TEXT + ";" +
                        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                        "-fx-padding: 10 22 10 22; -fx-background-radius: 20;" +
                        "-fx-border-color: " + AMAZON_GOLD_BORDER + "; -fx-border-radius: 20; -fx-cursor: hand;";
        String hover =
                "-fx-background-color: " + AMAZON_GOLD_HOVER + "; -fx-text-fill: " + AMAZON_DARK_TEXT + ";" +
                        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                        "-fx-padding: 10 22 10 22; -fx-background-radius: 20;" +
                        "-fx-border-color: #F2C200; -fx-border-radius: 20; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    private static String baseButtonStyle(String bg, String textColor) {
        return "-fx-background-color: " + bg + "; -fx-text-fill: " + textColor + ";" +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                "-fx-padding: 10 22 10 22; -fx-background-radius: 12; -fx-cursor: hand;";
    }

    // ─── Status / Feedback Labels ─────────────────────────────────────────────

    /** Inline status label. Pass one of: "success", "error", "warn". */
    public static Label statusLabel(String message, String type) {
        Label lbl = new Label(message);
        String color, bg, border;
        switch (type) {
            case "success": color = GREEN_SUCCESS; bg = GREEN_BG; border = "#BBF7D0"; break;
            case "error":   color = RED_ERROR;     bg = RED_BG;   border = "#FECACA"; break;
            default:        color = AMBER_WARN;    bg = AMBER_BG; border = "#FDE68A"; break;
        }
        lbl.setStyle(
                "-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;" +
                        "-fx-font-family: 'Segoe UI'; -fx-background-color: " + bg + ";" +
                        "-fx-border-color: " + border + "; -fx-border-radius: 8px; -fx-background-radius: 8px;" +
                        "-fx-padding: 7 14 7 14;"
        );
        lbl.setWrapText(true);
        return lbl;
    }

    // ─── Cards ────────────────────────────────────────────────────────────────

    public static VBox shadowCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(22));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 18px; -fx-border-radius: 18px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(124,92,252,0.08), 16, 0, 0, 4);"
        );
        return card;
    }

    public static VBox plainCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(22));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 18px; -fx-border-radius: 18px;" +
                        "-fx-border-color: " + BORDER_LIGHT + "; -fx-border-width: 1.5px;"
        );
        return card;
    }

    /** Card with a colored left accent stripe. */
    public static HBox accentCard(String accentColor) {
        HBox outer = new HBox();
        outer.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14px; -fx-border-radius: 14px;" +
                        "-fx-border-color: " + BORDER_LIGHT + "; -fx-border-width: 1px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 2);"
        );
        javafx.scene.layout.Region stripe = new javafx.scene.layout.Region();
        stripe.setPrefWidth(5);
        stripe.setStyle(
                "-fx-background-color: " + accentColor + ";" +
                        "-fx-background-radius: 14px 0 0 14px;"
        );
        outer.getChildren().add(stripe);
        return outer;
    }

    // ─── Tables ───────────────────────────────────────────────────────────────

    public static void styleTable(TableView<?> table) {
        table.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12;" +
                        "-fx-border-color: " + BORDER_LIGHT + "; -fx-border-width: 1px;" +
                        "-fx-selection-bar: " + PURPLE_SOFT + "; -fx-table-cell-border-color: #F4F1FC;"
        );
        table.setFixedCellSize(42);
        table.setPrefHeight(220);
    }

    /** Table with a fixed preferred height. */
    public static void styleTable(TableView<?> table, int prefHeight) {
        styleTable(table);
        table.setPrefHeight(prefHeight);
    }

    // ─── Icons / Graphics ─────────────────────────────────────────────────────

    public static SVGPath createSearchIcon(Color fill) {
        SVGPath svg = new SVGPath();
        svg.setContent("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        svg.setFill(fill);
        return svg;
    }

    public static SVGPath createCartIcon(Color fill) {
        SVGPath svg = new SVGPath();
        svg.setContent("M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.06 1.4-2.54h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z");
        svg.setFill(fill);
        return svg;
    }

    public static HBox createRatingStars(int fullStars) {
        HBox row = new HBox(3);
        row.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < 5; i++) {
            SVGPath star = new SVGPath();
            star.setContent("M6 0l1.8 3.7 4.2.6-3 3 1 4.2-4-2-4 2 1-4.2-3-3 4.2-.6z");
            star.setFill(i < fullStars ? Color.web("#F2C200") : Color.web("#D1D1D1"));
            row.getChildren().add(star);
        }
        return row;
    }

    // ─── Image Loading ────────────────────────────────────────────────────────

    public static Image loadImage(String resourcePath) {
        if (resourcePath == null || resourcePath.trim().isEmpty()) return loadFallbackImage();
        try {
            InputStream is = UIHelper.class.getResourceAsStream(resourcePath);
            if (is != null) return new Image(is);
        } catch (Exception ignored) {}
        return loadFallbackImage();
    }

    private static Image loadFallbackImage() {
        try {
            InputStream is = UIHelper.class.getResourceAsStream("/images/products/fallback.png");
            if (is != null) return new Image(is);
        } catch (Exception e) {
            System.err.println("[UIHelper-ERROR] Critical: fallback image missing.");
        }
        return null;
    }
}