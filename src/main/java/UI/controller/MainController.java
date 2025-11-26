package UI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.Objects;

public class MainController {
    //attributi FXML
    @FXML private Button backToMain;

    @FXML private Button userProfileButton;

    @FXML private StackPane contentArea;

    @FXML
    private void initialize() {
        //carica la prima view
        loadView("flight-search.fxml");
        backToMain.setOnAction(event -> {
            loadView("flight-search.fxml");
        });
        userProfileButton.setOnAction(event -> {
            loadView("user-profile-view.fxml");
        });
    }

    //funzione che consente di caricare un'altra view
    public void loadView(String fxmlName) {
        try {
            Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/UI/view/" + fxmlName))
            );
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
