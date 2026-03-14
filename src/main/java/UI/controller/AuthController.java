package UI.controller;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import service.AuthService;

import dto.user.SignUpRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController implements NavigatorAware {
    //attributi
    Navigator navigator;
    AuthService authService = new AuthService();

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private void initialize() {

    }

    @Override
    public void setNavigator(Navigator navigator){ //funzione utilizzata da MainController per sfruttare l'oggetto Navigator e chiamare loadView
        this.navigator = navigator;
    }

    @FXML
    private void openSignUpPage() {
        navigator.loadView("sign-up-view.fxml");
    }

    @FXML
    private void backToLogin() {
        navigator.loadView("login-view.fxml");
    }

    @FXML
    private void createSignUpRequest() {
        SignUpRequest request = new SignUpRequest();

        request.setUsername(usernameField.getText());
        request.setEmail(emailField.getText());
        request.setRawPassword(passwordField.getText());
        request.setName(nameField.getText());
        request.setSurname(surnameField.getText());

        authService.saveUser(request);
    }
}
