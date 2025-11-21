package domain.user;

import java.time.LocalDate;

public class Passenger extends Person {
    //attributi
    private int passengerId;
    private User companionOwner;

    //costruttore
    public Passenger(String name, String surname, LocalDate dateOfBirth, String address, String city, String province,
                     String country, String codFisc, String codId, String phoneNumber, int passengerId, User companionOwner) {

        super(name, surname, dateOfBirth,  address,
                city, province, country, codFisc, codId,
                phoneNumber);
        this.passengerId = passengerId;
        this.companionOwner = companionOwner;
    }

    //metodi
    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public User getCompanionOwner() {
        return companionOwner;
    }

    public void setCompanionOwner(User companionOwner) {
        this.companionOwner = companionOwner;
    }
}
