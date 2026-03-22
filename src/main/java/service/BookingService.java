package service;

import domain.flight.Seat;
import domain.reservation.Reservation;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import persistence.dao.flight.SeatDAO;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.reservation.SeatReservationDAO;
import persistence.dao.user.PassengerDAO;
import persistence.dao.user.UserDAO;
import util.session.BookingSession;
import util.session.SessionHandler;

import java.util.ArrayList;

public class BookingService {
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final SeatReservationDAO seatReservationDAO = new SeatReservationDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final UserDAO userDAO = new UserDAO();
    private final BookingSession session = BookingSession.getInstance();
    private final Reservation bookingReservation = new Reservation();


    public void saveSelfPassenger(Passenger passenger) {
        SessionHandler.getInstance().getCurrentUser().setSelfPassenger(passenger);
    }

    public void saveSessionPassengers() {
        session.setPassengers(new ArrayList<>(session.getPassengerBySeatCode().values()));
    }

    public void mapPassengersAndSeats(String seatCode, Passenger passenger) {
        session.addMappedElement(seatCode, passenger);
    }

    public void createSeatReservations() {
        for(Seat s: session.getSelectedSeats()) {
            Passenger currentPassenger = session.getPassengerBySeatCode().get(s.getSeatCode());
            SeatReservation currentReservation = new SeatReservation();
            currentReservation.setSeat(s);
            currentReservation.setPassenger(currentPassenger);
            session.addSeatReservation(currentReservation);
        }
    }

    public void saveBookingData() {
        savePassengers();
        saveReservation();
        saveSeatReservations();
        userDAO.save(SessionHandler.getInstance().getCurrentUser());
    }

    private void saveReservation() {
        bookingReservation.setFlight(session.getSelectedFlight());
        bookingReservation.setUser(SessionHandler.getInstance().getCurrentUser());
        bookingReservation.confirm();

        reservationDAO.save(bookingReservation);
    }

    private void savePassengers() {
        for (Passenger p : session.getPassengerBySeatCode().values()) {
            passengerDAO.save(p);
            System.out.println("Passenger salvato: " + p + " id=" + p.getPassengerId());
        }
    }

    private void saveSeatReservations() {
        for(SeatReservation sr : session.getSeatReservations()) {
            sr.setReservation(bookingReservation);
            sr.confirm();
            seatReservationDAO.save(sr);
        }
    }
}
