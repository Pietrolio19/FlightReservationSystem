package UI.controller;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import util.session.SessionHandler;

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
        updateAuthButton();
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

    @Override
    public void refreshAuthUI() {
        updateAuthButton();
    }

    private void updateAuthButton() {
        if(SessionHandler.getInstance().isLoggedIn()) {
            userProfileButton.setText("Profilo");
            userProfileButton.setOnAction(e -> loadView("user-profile-view.fxml"));
        }
        else {
            userProfileButton.setText("Accedi");
            userProfileButton.setOnAction(e -> loadView("login-view.fxml"));
        }
    }
}
