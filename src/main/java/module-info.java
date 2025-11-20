module com.example.flightreservationsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.flightreservationsystem to javafx.fxml;
    exports com.example.flightreservationsystem;
}