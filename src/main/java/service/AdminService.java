package service;

import domain.flight.Flight;
import domain.flight.Seat;
import domain.reservation.Reservation;
import domain.reservation.SeatReservation;
import domain.user.User;
import persistence.dao.flight.FlightDAO;
import persistence.dao.flight.SeatDAO;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.reservation.SeatReservationDAO;
import persistence.dao.user.PassengerDAO;
import persistence.dao.user.UserDAO;

import java.util.List;

public class AdminService {
    private final FlightDAO flightDAO = new FlightDAO();
    private final FlightService flightService = new FlightService();
    private final UserDAO userDAO = new UserDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final SeatReservationDAO seatReservationDAO = new SeatReservationDAO();
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final SeatDAO seatDAO = new SeatDAO();

    public List<Reservation> findAllReservation() {
        List<Reservation> current = reservationDAO.findAll();

        for (Reservation reservation : current) {
            Long flightId = reservation.getFlight().getFlightId();
            Long userId = reservation.getUser().getUserId();
            Flight currentFlight = flightDAO.findById(flightId).orElseThrow(() -> new IllegalArgumentException("Volo non valido"));
            User currentUser = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

            reservation.setFlight(flightService.objectMapper(currentFlight));
            reservation.setUser(currentUser);
        }
        return current;
    }

    public List<Flight> findAllFlights() {
        return flightService.getFlightList();
    }

    public List<SeatReservation> findAllSeatReservation() {
        List<SeatReservation> current = seatReservationDAO.findAll();

        for(SeatReservation sr : current) {
            Long seatId = sr.getSeat().getSeatId();
            Long passengerId = sr.getPassenger().getPassengerId();
            Long reservationId = sr.getReservation().getReservationId();

            sr.setSeat(seatDAO.findById(seatId).orElseThrow(() -> new IllegalArgumentException("Posto non trovato")));
            sr.setPassenger(passengerDAO.findById(passengerId).orElseThrow(() -> new IllegalArgumentException("Passeggero non trovato")));
            sr.setReservation(reservationDAO.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata")));

        }
        return current;
    }
}
