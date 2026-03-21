package service;

import domain.flight.Seat;
import domain.reservation.Reservation;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import persistence.dao.flight.SeatDAO;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.reservation.SeatReservationDAO;
import persistence.dao.user.PassengerDAO;
import util.session.BookingSession;
import util.session.SessionHandler;

import java.util.ArrayList;
import java.util.Map;

public class BookingService {
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final SeatReservationDAO seatReservationDAO = new SeatReservationDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final BookingSession session = BookingSession.getInstance();
    private final Reservation bookingReservation = new Reservation();

    public void saveSessionPassengers(Map<String, Passenger> passengerMap) {
        session.setPassengers(new ArrayList<>(passengerMap.values()));//.values() restituisce una Collection<Passenger>
    }

    public void createSeatReservations(Map<String, Passenger> passengerMap) {
        for(Seat s: session.getSelectedSeats()) {
            Passenger currentPassenger = passengerMap.get(s.getSeatCode());
            SeatReservation currentReservation = new SeatReservation();
            currentReservation.setSeat(s);
            currentReservation.setPassenger(currentPassenger);
            session.addSeatReservation(currentReservation);
        }
    }

    public void saveBookingData() {
        saveReservation();
        savePassengers();
        saveSeats();
        saveSeatReservations();
    }

    private void saveReservation() {
        bookingReservation.setFlight(session.getSelectedFlight());
        bookingReservation.setUser(SessionHandler.getInstance().getCurrentUser());
        bookingReservation.confirm();

        reservationDAO.insert(bookingReservation);
    }

    private void savePassengers() {
        for(Passenger p : session.getPassengers()) {
            passengerDAO.insert(p);
        }
    }

    private void saveSeats() {
        for(Seat s : session.getSelectedSeats()) {
            seatDAO.insert(s);
        }
    }

    private void saveSeatReservations() {
        for(SeatReservation sr : session.getSeatReservations()) {
            sr.setReservation(bookingReservation);
            seatReservationDAO.insert(sr);
        }
    }
}
