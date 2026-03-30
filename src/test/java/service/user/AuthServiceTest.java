package service.user;

import domain.user.User;
import dto.user.LoginRequest;
import dto.user.SignUpRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.dao.user.UserDAO;
import util.security.PasswordHasher;
import util.session.SessionHandler;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    private User user;

    private AuthService authService;
    private SessionHandler session = SessionHandler.getInstance();

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        user = new User();

        authService = new AuthService();

        userDAO = new UserDAO();

        user.setUserId(null);
        user.setUsername("user_test_passenger");
        user.setEmail("user_test_passenger@example.com");
        user.setHashPassword("hashed_password_test");
    }

    @AfterEach
    void cleanTestData() {
        if(user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
        }
    }

    @Test
    void registerUser_correctly_register_user() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());
        request.setRawPassword(user.getHashPassword());

        authService.registerUser(request);
        User testUser = userDAO.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        user.setUserId(testUser.getUserId());

        assertEquals(request.getUsername(), testUser.getUsername());
        assertEquals(request.getEmail(), testUser.getEmail());
        assertNotEquals("hashed_password_test", testUser.getHashPassword());
        assertTrue(PasswordHasher.verify("hashed_password_test", testUser.getHashPassword()));

        assertTrue(session.isLoggedIn());
    }

    @Test
    void registerUser_with_short_password_throws_exception() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());
        request.setRawPassword("hash");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerUser(request)
        );

        assertEquals("La password deve contenere almeno 8 caratteri", ex.getMessage());
    }

    @Test
    void registerUser_with_existing_email_throws_exception() {
        userDAO.insert(user);

        SignUpRequest request = new SignUpRequest();
        request.setUsername("exceptionTest");
        request.setEmail(user.getEmail());
        request.setRawPassword(user.getHashPassword());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerUser(request)
        );

        assertEquals("Email già in uso", ex.getMessage());
    }

    @Test
    void registerUser_with_existing_username_throws_exception() {
        userDAO.insert(user);

        SignUpRequest request = new SignUpRequest();
        request.setUsername(user.getUsername());
        request.setEmail("exception@test.com");
        request.setRawPassword(user.getHashPassword());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerUser(request)
        );

        assertEquals("Username già in uso", ex.getMessage());
    }

    @Test
    void loginUser_correctly_login_user() {
        String rawPassword = "hashed_password_test";

        user.setHashPassword(PasswordHasher.hash(rawPassword));
        userDAO.insert(user);

        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setRawPassword(rawPassword);

        authService.loginUser(request);

        assertTrue(session.isLoggedIn());
    }

    @Test
    void loginUser_null_email_throws_exception() {
        LoginRequest request = new LoginRequest();
        request.setEmail(null);
        request.setRawPassword("hashed_password_test");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.loginUser(request)
        );

        assertEquals("Email obbligatoria", ex.getMessage());
    }
}
