package domain.user;

import java.util.ArrayList;

enum FidelityStatus {BRONZE, SILVER, GOLD, PLATINUM}
enum UserRole {USER, ADMIN}

public class User {
    //attributi
    private Long userId;
    private String username;
    private String email;
    private String hashPassword;
    private int fidelityPoints = 0;
    private FidelityStatus fidelityStatus =  FidelityStatus.BRONZE;
    private final ArrayList<Passenger> companions =  new ArrayList<>();
    private Passenger selfPassenger; //lo user ha un passeggero associato per i dati anagrafici
    private UserRole userRole = UserRole.USER;

    //costruttori
    public User(){}

    public User(Long userId, String username, String email, String hashPassword) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.hashPassword = hashPassword;
    }

    //metodi
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public int getFidelityPoints() {
        return fidelityPoints;
    }

    public void addFidelityPoints(int fidelityPoints) {
        this.fidelityPoints += fidelityPoints;
    }

    public String getFidelityStatus() {
        return fidelityStatus != null ? fidelityStatus.toString() : null;
    }

    //setter per chi non vede l'enum
    public void setFidelityStatus(String status) {
        if (status != null) {
            this.fidelityStatus = FidelityStatus.valueOf(status);
        } else {
            this.fidelityStatus = null;
        }
    }

    public ArrayList<Passenger> getCompanions() {
        return companions;
    }

    public void addCompanion(Passenger companion) {
        this.companions.add(companion);
        companion.setCompanionOwner(this);
    }

    public void removeCompanion(Passenger companion) {
        this.companions.remove(companion);
        companion.setCompanionOwner(null);
    }

    public void calculateFidelityStatus() {
        if (this.fidelityPoints >= 10000) {
            this.fidelityStatus = FidelityStatus.PLATINUM;
        } else if (this.fidelityPoints >= 5000) {
            this.fidelityStatus = FidelityStatus.GOLD;
        } else if (this.fidelityPoints >= 2000) {
            this.fidelityStatus = FidelityStatus.SILVER;
        } else {
            this.fidelityStatus = FidelityStatus.BRONZE;
        }
    }

    public Passenger getSelfPassenger() {
        return selfPassenger;
    }

    public void setSelfPassenger(Passenger selfPassenger) {
        this.selfPassenger = selfPassenger;
    }

    public String getUserRole() {
        return userRole != null ? userRole.toString() : null;
    }

    public void setUserRole(String role) {
        if(role != null) {
            this.userRole = UserRole.valueOf(role);
        } else {
            this.userRole = null;
        }
    }
}
