module flightreservationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;

    opens UI to javafx.fxml;
    opens UI.controller to javafx.fxml;
    exports UI;
    exports UI.controller;
    exports UI.controller.user;
    opens UI.controller.user to javafx.fxml;
    exports UI.controller.flight;
    opens UI.controller.flight to javafx.fxml;
}