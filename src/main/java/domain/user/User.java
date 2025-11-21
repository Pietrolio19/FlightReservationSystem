package domain.user;

import java.time.LocalDate;
import java.util.ArrayList;

enum FidelityStatus {BRONZE, SILVER, GOLD, PLATINUM}

public class User extends Person {
    //attributi
    private int userId;
    private String username;
    private String email;
    private String hashPassword;
    private int fidelityPoints = 0;
    private FidelityStatus fidelityStatus =  FidelityStatus.BRONZE;
    private ArrayList<Passenger> companions =  new ArrayList<>();

    //costruttore
    public User(String name, String surname, LocalDate dateOfBirth, String address, String city, String province,
                String country, String codFisc, String codId, String phoneNumber,int userId, String username,
                String email, String hashPassword) {

        super(name, surname, dateOfBirth,  address,
              city, province, country, codFisc, codId,
              phoneNumber);
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.hashPassword = hashPassword;
    }

    //metodi
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public void setFidelityPoints(int fidelityPoints) {
        this.fidelityPoints = fidelityPoints;
    }

    public FidelityStatus getFidelityStatus() {
        return fidelityStatus;
    }

    public void setFidelityStatus(FidelityStatus fidelityStatus) {
        this.fidelityStatus = fidelityStatus;
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
        if (this.fidelityPoints >= 100000) {
            this.fidelityStatus = FidelityStatus.PLATINUM;
        } else if (this.fidelityPoints >= 50000) {
            this.fidelityStatus = FidelityStatus.GOLD;
        } else if (this.fidelityPoints >= 20000) {
            this.fidelityStatus = FidelityStatus.SILVER;
        } else {
            this.fidelityStatus = FidelityStatus.BRONZE;
        }
    }
}
