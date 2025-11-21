package domain.flight;

public class Airport {
    //attributi
    private int iata;
    private String city;
    private String country;
    private String name;

    //costruttore
    public Airport(int iata, String city, String country, String name) {
        this.iata = iata;
        this.city = city;
        this.country = country;
        this.name = name;
    }

    //metodi
    public int getIata() {
        return iata;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

