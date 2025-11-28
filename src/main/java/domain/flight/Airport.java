package domain.flight;

public class Airport {
    //attributi
    private int airportId;
    private String iata;
    private String city;
    private String country;
    private String name;

    //costruttori
    public Airport(){}

    public Airport(int id, String iata, String city, String country, String name) {
        this.airportId = id;
        this.iata = iata;
        this.city = city;
        this.country = country;
        this.name = name;
    }

    //metodi
    public int getAirportId() {
        return airportId;
    }

    public void  setAirportId(int airportId) {
        this.airportId = airportId;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
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

