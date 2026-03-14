package dto.flight;

import domain.flight.Flight;

import java.util.ArrayList;
import java.util.List;

public class FlightSearchResult {
    List<Flight> outwardFlights = new ArrayList<>();
    List<Flight> returnFlights = new ArrayList<>();

    public List<Flight> getOutwardFlights() {
        return outwardFlights;
    }

    public void setOutwardFlights(List<Flight> outwardFlights) {
        this.outwardFlights = outwardFlights;
    }

    public List<Flight> getReturnFlights() {
        return returnFlights;
    }

    public void setReturnFlights(List<Flight> returnFlights) {
        this.returnFlights = returnFlights;
    }
}
