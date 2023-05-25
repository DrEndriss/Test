module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires org.controlsfx.controls;

    requires java.sql;
    requires org.apache.poi.ooxml;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.handler;
}