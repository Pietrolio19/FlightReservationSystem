package service.user;

import domain.user.User;
import dto.user.LoginRequest;
import dto.user.SignUpRequest;
import persistence.dao.user.UserDAO;
import util.security.PasswordHasher;
import util.session.SessionHandler;


public class AuthService {
    UserDAO userDAO = new UserDAO();

    public void registerUser(SignUpRequest request) {
        validateSignUpRequest(request);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        String hashedPassword = PasswordHasher.hash(request.getRawPassword());
        user.setHashPassword(hashedPassword);

        userDAO.insert(user);
        SessionHandler.getInstance().login(user);
    }

    public void loginUser(LoginRequest request) {
        validateLogin(request);
        User user = userDAO.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Email o password errati"));

        if (!PasswordHasher.verify(request.getRawPassword(), user.getHashPassword())) {
            throw new IllegalArgumentException("Email o password errati");
        }

        SessionHandler.getInstance().login(user);
    }

    private void validateLogin(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email obbligatoria");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email non valida");
        }

        if (request.getRawPassword() == null ) {
            throw new IllegalArgumentException("Password obbligatoria");
        }
    }

    private void validateSignUpRequest(SignUpRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username obbligatorio");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email obbligatoria");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email non valida");
        }

        if (request.getRawPassword() == null || request.getRawPassword().length() < 8) {
            throw new IllegalArgumentException("La password deve contenere almeno 8 caratteri");
        }

        if (userDAO.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username già in uso");
        }

        if (userDAO.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email già in uso");
        }
    }
}
