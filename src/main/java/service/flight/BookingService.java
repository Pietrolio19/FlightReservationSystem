package service.flight;

import domain.flight.Flight;
import domain.flight.Seat;
import domain.reservation.Reservation;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import dto.flight.SeatState;
import persistence.dao.flight.SeatDAO;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.reservation.SeatReservationDAO;
import persistence.dao.user.PassengerDAO;
import persistence.dao.user.UserDAO;
import util.session.BookingSession;
import util.session.SessionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingService {
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final SeatReservationDAO seatReservationDAO = new SeatReservationDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final UserDAO userDAO = new UserDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final BookingSession session = BookingSession.getInstance();
    private final Reservation bookingReservation = new Reservation();

    public void saveSelfPassenger(Passenger passenger) {
        SessionHandler.getInstance().getCurrentUser().setSelfPassenger(passenger);
    }

    public List<Seat> getSessionSeats() {
        return session.getSelectedSeats();
    }

    public void saveSessionPassengers() {
        session.setPassengers(new ArrayList<>(session.getPassengerBySeatCode().values()));
    }

    public void mapPassengersAndSeats(String seatCode, Passenger passenger) {
        session.addMappedElement(seatCode, passenger);
    }

    public void createSeatReservations() {
        session.clearSeatReservations();
        for(Seat s: session.getSelectedSeats()) {
            Passenger currentPassenger = session.getPassengerBySeatCode().get(s.getSeatCode());
            SeatReservation currentReservation = new SeatReservation();
            currentReservation.setSeat(s);
            currentReservation.setPassenger(currentPassenger);
            session.addSeatReservation(currentReservation);
        }
    }

    public Flight getSessionFlight() {
        return session.currentFlight();
    }

    public List<SeatReservation> getSessionSeatReservations() {
        return session.getSeatReservations();
    }

    public List<SeatState> getSeatStates(Long id) {
        return seatDAO.getSeatStateByFlightId(id);
    }

    public int getTotalPassengers() {
        return session.getTotalPassengers();
    }

    public void addSessionSeat(Seat seat) {
        session.addSeat(seat);
    }

    public void removeSessionSeat(Seat seat) {
        session.removeSeat(seat);
    }

    public void clearSessionSeats() {
        session.clearSeats();
    }
    public void clearSessionPassengers() {
        session.clearPassengers();
    }

    public void saveBookingData() {
        savePassengers();
        saveReservation();
        saveSeatReservations();
        saveUser();
    }

    private void saveReservation() {
        bookingReservation.setFlight(session.currentFlight());
        bookingReservation.setUser(SessionHandler.getInstance().getCurrentUser());
        bookingReservation.confirm();

        reservationDAO.save(bookingReservation);
    }

    private void savePassengers() {
        for (Passenger p : session.getPassengerBySeatCode().values()) {
            Optional<Passenger> existing = passengerDAO.findByCodFiscOrCodId(p.getCodFisc(), p.getCodId());

            existing.ifPresent(current -> {
                p.setPassengerId(current.getPassengerId());

                if (p.getCompanionOwner() == null) {
                    p.setCompanionOwner(current.getCompanionOwner());
                }
            });

            passengerDAO.save(p);
        }
    }

    private void saveSeatReservations() {
        for(SeatReservation sr : session.getSeatReservations()) {
            sr.setReservation(bookingReservation);
            sr.confirm();
            seatReservationDAO.save(sr);
        }
    }

    private void saveUser() {
        int totalPrice = 0;
        for(Seat s : session.getSelectedSeats()){
            totalPrice += s.getPrice();
        }
        SessionHandler.getInstance().getCurrentUser().addFidelityPoints(totalPrice);
        SessionHandler.getInstance().getCurrentUser().calculateFidelityStatus();
        userDAO.save(SessionHandler.getInstance().getCurrentUser());
    }

    public Reservation getBookingReservation() {
        return bookingReservation;
    }
}
