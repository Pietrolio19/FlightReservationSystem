package UI.controller.admin;

import domain.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import persistence.dao.user.UserDAO;

public class AdminUserController {
    private final UserDAO userDAO = new UserDAO();

    @FXML
    private ListView<User> userListView;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private void initialize() {
        loadUserInfo();
    }

    private void loadUserInfo() {
        ObservableList<User> current = FXCollections.observableList(userDAO.findAll());
        userListView.setItems(current);
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showRecordContent(newValue);
            }
        });
    }

    private void showRecordContent(User user) {
        usernameLabel.setText("Username: " + user.getUsername());
        emailLabel.setText("Email: " + user.getEmail());
    }
}
