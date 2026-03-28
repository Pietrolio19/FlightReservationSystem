package util.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {
    @Test
    void hash_should_return_non_null_and_different_string() {
        String rawPassword = "mypassword123";

        String hashedPassword = PasswordHasher.hash(rawPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
    }

    @Test
    void verify_should_return_true_with_correct_password() {
        String rawPassword = "mypassword123";
        String hashedPassword = PasswordHasher.hash(rawPassword);

        boolean result = PasswordHasher.verify(rawPassword, hashedPassword);

        assertTrue(result);
    }

    @Test
    void verify_should_return_false_with_wrong_password() {
        String rawPassword = "mypassword123";
        String hashedPassword = PasswordHasher.hash(rawPassword);

        boolean result = PasswordHasher.verify("wrongpassword", hashedPassword);

        assertFalse(result);
    }
}
