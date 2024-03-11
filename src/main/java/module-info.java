module com.example.examendi2trimestre {
    requires javafx.controls;
    requires javafx.fxml;
    requires jasperreports;
    requires java.sql;

    opens com.example.examendi2trimestre to javafx.fxml;
    exports com.example.examendi2trimestre;
}