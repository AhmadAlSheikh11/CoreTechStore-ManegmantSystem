module com.example.dbsph4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens com.example.dbsph4.models to javafx.base, javafx.controls;
    opens com.example.dbsph4.gui to javafx.graphics, javafx.controls, javafx.base;
    opens com.example.dbsph4.dao to java.sql;
    opens com.example.dbsph4 to javafx.fxml;
    exports com.example.dbsph4;
}