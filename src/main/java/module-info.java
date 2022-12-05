module com.example.median_cut {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.median_cut to javafx.fxml;
    exports com.example.median_cut;
}