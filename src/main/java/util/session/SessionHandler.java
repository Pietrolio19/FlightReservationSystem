package util.session;

import domain.user.User;

public class SessionHandler { //singleton per avere un'unica sessione condivisa da tutti
    private static SessionHandler instance = new SessionHandler();
    private User currentUser;

    private SessionHandler() {
        this.currentUser = null;
    }

    public void login(User user){
        currentUser = user;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public static SessionHandler getInstance() {
        return instance;
    }
}
