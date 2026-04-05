package UI.controller;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import util.session.BookingSession;
import util.session.SessionHandler;

import java.io.IOException;

public class MainController implements Navigator {
    //attributi FXML
    @FXML
    private Button backToMain;

    @FXML
    private Button userProfileButton;

    @FXML
    private Button adminButton;

    @FXML
    private StackPane contentArea;

    @FXML
    private void initialize() {
        loadView("flight-search.fxml");
        backToMain.setOnAction(e -> {
            loadView("flight-search.fxml");
            BookingSession.getInstance().clearTotal();
            BookingSession.getInstance().clear();
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
            if(SessionHandler.getInstance().getCurrentUser().getUserRole().equals("ADMIN")){
                adminButton.setVisible(true);
                adminButton.setManaged(true);
                adminButton.setOnAction(e -> loadView("admin-view.fxml"));
            }
        }
        else {
            userProfileButton.setText("Accedi");
            userProfileButton.setOnAction(e -> loadView("login-view.fxml"));
        }
    }
}
