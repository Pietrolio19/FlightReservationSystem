package service;

import domain.flight.Flight;

public class SeatReservationService {
    //attributi
    Flight currentFlight;

    public void setCurrentFlight(Flight flight) {
        this.currentFlight = flight;
    }

    public Flight getCurrentFlight() {
        return currentFlight;
    }
}
