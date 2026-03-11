package domain.user;

import java.time.LocalDate;

public class Person {
    //attributi
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String province;
    private String country;
    private String codFisc;
    private String codId;
    private String phoneNumber;

    //costruttori
    public Person() {}

    public Person(String name, String surname, LocalDate dateOfBirth, String address,
                  String city, String province, String country, String codFisc,
                  String codId, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.city = city;
        this.province = province;
        this.country = country;
        this.codFisc = codFisc;
        this.codId = codId;
        this.phoneNumber = phoneNumber;
    }

    //metodi
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCodFisc() {
        return codFisc;
    }

    public void setCodFisc(String codFisc) {
        this.codFisc = codFisc;
    }

    public String getCodId() {
        return codId;
    }

    public void setCodId(String codId) {
        this.codId = codId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
