package domain.flight;

public class Airline {
    //attributi
    private long airlineId;
    private String name;
    private String iata;
    private String icao;
    private String country;

    //costruttori
    public Airline(){}

    public Airline(long airlineId, String name, String iata, String icao, String country) {
        this.airlineId = airlineId;
        this.name = name;
        this.iata = iata;
        this.icao = icao;
        this.country = country;
    }

    //metodi
    public long getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(long airlineId) {
        this.airlineId = airlineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
