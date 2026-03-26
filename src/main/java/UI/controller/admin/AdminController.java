package UI.controller.admin;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdminController implements NavigatorAware {
    //attributi
    private Navigator navigator;

    @FXML
    private HBox mainBox;

    @FXML
    private VBox leftBox;

    @FXML
    private AnchorPane viewArea;

    @FXML
    private Button backToMain;

    @FXML
    private Button userButton;

    @FXML
    private Button flightButton;

    @FXML
    private Button reservationButton;

    @FXML
    private Button seatButton;

    @FXML
    private Button passengerButton;

    @FXML
    private void initialize() {
        leftBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.2));
        viewArea.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.8));
        backToMain.setOnAction(e -> navigator.loadView("flight-search.fxml"));
        userButton.setOnAction(e -> loadView("admin/admin-user-view.fxml"));
        flightButton.setOnAction(e -> loadView("admin/admin-flight-view.fxml"));
        reservationButton.setOnAction(e -> loadView("admin/admin-reservation-view.fxml"));
        seatButton.setOnAction(e -> loadView("admin/admin-seat-reservation-view.fxml"));
        passengerButton.setOnAction(e -> loadView("admin/admin-passenger-view.fxml"));
    }

    public void setNavigator(Navigator navigator){
        this.navigator = navigator;
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/view/" + fxmlPath));
            Parent view = loader.load();
            viewArea.getChildren().setAll(view);

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
