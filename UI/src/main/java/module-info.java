module com.example.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens com.example.ui to javafx.fxml;
    exports com.example.ui;
}