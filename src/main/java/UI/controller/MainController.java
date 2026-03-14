package UI.controller;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.Objects;

public class MainController implements Navigator {
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
            loadView("login-view.fxml");
        });
    }

    //funzione che consente di caricare un'altra view
    @Override
    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/view/" + fxmlPath));
            Parent view = loader.load();

            Object controller = loader.getController();
            if(controller instanceof NavigatorAware aware)
                aware.setNavigator(this);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
