module com.example.fisheatfish {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;

    exports com.example.fisheatfish;
    opens com.example.fisheatfish to javafx.fxml;
}
