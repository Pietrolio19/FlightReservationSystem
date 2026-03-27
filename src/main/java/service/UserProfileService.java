package service;

import domain.flight.Flight;
import domain.reservation.Reservation;
import domain.user.Passenger;
import domain.user.User;
import persistence.dao.flight.FlightDAO;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.user.PassengerDAO;
import util.session.SessionHandler;

import java.util.List;
import java.util.Optional;

public class UserProfileService {
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final FlightDAO flightDAO = new FlightDAO();
    private final FlightService flightService = new FlightService();
    private final SessionHandler session = SessionHandler.getInstance();

    public void getSelfPassengerInfo() {
        User currentUser = session.getCurrentUser();

        if (currentUser == null || currentUser.getSelfPassenger() == null) {
            return;
        }

        Long passengerId = currentUser.getSelfPassenger().getPassengerId();
        if (passengerId == null) {
            return;
        }

        Optional<Passenger> currentPassenger = passengerDAO.findById(passengerId);
        currentPassenger.ifPresent(currentUser::setSelfPassenger);
    }
    public List<Reservation> getUserReservations() {
        List<Reservation> reservationList = reservationDAO.findByUser(session.getCurrentUser().getUserId());

        for (Reservation reservation : reservationList) {
            Long flightId = reservation.getFlight().getFlightId();

            Flight currentFlight = flightDAO.findById(flightId).orElseThrow(() -> new IllegalArgumentException("Volo non valido"));

            reservation.setFlight(flightService.objectMapper(currentFlight));
        }

        return reservationList;
    }

    public List<Passenger> getUserCompanions() {
        List<Passenger> current =  passengerDAO.findByCompanionOwner(session.getCurrentUser().getUserId());
        for(Passenger p : current)
            session.getCurrentUser().addCompanion(p);
        return current;
    }
}
