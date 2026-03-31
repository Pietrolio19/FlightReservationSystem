package service.flight;

import domain.flight.Flight;
import domain.flight.Seat;
import dto.flight.SeatState;
import persistence.dao.flight.FlightDAO;
import persistence.dao.flight.SeatDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SeatReservationService {
    //attributi
    private final SeatDAO seatDAO = new SeatDAO();
    private final FlightDAO flightDAO = new FlightDAO();
    private Map<String, Seat> seatsMap = new HashMap<>();

    public List<Seat> getSeatsList(Long flightId) {
        Optional<Flight> optionalFlight = flightDAO.findById(flightId);
        Flight flight = optionalFlight.orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));

        seatsMap.clear(); //seatsMap mantiene i posti del volo corrente

        List<Seat> currentSeats = seatDAO.findByFlightId(flightId);
        for(Seat s: currentSeats) {
            s.setFlight(flight);
            seatsMap.put(s.getSeatCode(), s);
        }

        return currentSeats;
    }

    public List<SeatState> getSeatStates(Long id) {
        return seatDAO.getSeatStateByFlightId(id);
    }
}
