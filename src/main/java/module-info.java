module com.example.dbsph4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dbsph4 to javafx.fxml;
    exports com.example.dbsph4;
}