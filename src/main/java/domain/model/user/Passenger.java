package domain.model.user;

import java.time.LocalDate;

public class Passenger extends Person {
    //attributi
    private Long passengerId;
    private User companionOwner;

    //costruttori
    public Passenger(){}

    public Passenger(String name, String surname, LocalDate dateOfBirth, String address, String city, String province,
                     String country, String codFisc, String codId, String phoneNumber, Long passengerId, User companionOwner) {

        super(name, surname, dateOfBirth,  address,
                city, province, country, codFisc, codId,
                phoneNumber);
        this.passengerId = passengerId;
        this.companionOwner = companionOwner;
    }

    //metodi
    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public User getCompanionOwner() {
        return companionOwner;
    }

    public void setCompanionOwner(User companionOwner) {
        this.companionOwner = companionOwner;
    }
}
