package UI.controller.user;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import dto.user.LoginRequest;
import javafx.scene.control.Label;
import service.AuthService;
import dto.user.SignUpRequest;

import javafx.fxml.FXML;
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
    private Label errorLabel;

    @FXML
    private Label loginLabel;

    @FXML
    private void initialize() {
        loginLabel.setVisible(true);
        loginLabel.setManaged(true);
        loginLabel.setText("Per favore esegui il login");
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
    private void handleSignUpRequest() {
        SignUpRequest request = new SignUpRequest();

        request.setUsername(usernameField.getText());
        request.setEmail(emailField.getText());
        request.setRawPassword(passwordField.getText());

        try{
            authService.registerUser(request);
            errorLabel.setText("");
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
            navigator.refreshAuthUI();
            navigator.loadView("flight-search.fxml");
        } catch (IllegalArgumentException e){
            errorLabel.setText(e.getMessage());
            errorLabel.setManaged(true);
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleLoginRequest() {
        LoginRequest request = new LoginRequest();

        request.setEmail(emailField.getText().trim());
        request.setRawPassword(passwordField.getText());

        try{
            authService.loginUser(request);
            errorLabel.setText("");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            navigator.refreshAuthUI();
            navigator.loadView("user-profile-view.fxml");
        } catch(IllegalArgumentException e) {
            loginLabel.setManaged(false);
            loginLabel.setVisible(false);
            errorLabel.setText(e.getMessage());
            errorLabel.setManaged(true);
            errorLabel.setVisible(true);
        }
    }
}
