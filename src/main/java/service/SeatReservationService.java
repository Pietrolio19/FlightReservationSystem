package service;

import domain.flight.Flight;
import domain.flight.Seat;
import persistence.dao.flight.FlightDAO;
import persistence.dao.flight.SeatDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SeatReservationService {
    //attributi
    SeatDAO seatDAO = new SeatDAO();
    FlightDAO flightDAO = new FlightDAO();
    private final Map<String, Seat> seats = new HashMap<>();

    public List<Seat> getSeatsList(Long flightId) {
        Optional<Flight> optionalFlight = flightDAO.findById(flightId);
        Flight flight = optionalFlight.orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));

        seats.clear(); //seats mantiene i posti del volo corrente per cui è stata fatta la query

        List<Seat> currentSeats = seatDAO.findByFlightId(flightId);
        for(Seat s: currentSeats) {
            s.setFlight(flight);
            seats.put(s.getSeatCode(), s);
        }

        return currentSeats;
    }

    public Seat getSeatFromCode(String seatCode) {
        return seats.get(seatCode);
    }
}
