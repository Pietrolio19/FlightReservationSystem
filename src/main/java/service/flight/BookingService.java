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

import java.awt.*;
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
    private Reservation bookingReservation;

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
        return session.getSelectedFlight();
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
        Reservation reservation = saveReservation();
        saveSeatReservations(reservation);
        saveUser();
    }

    private Reservation saveReservation() {
        Reservation reservation = new Reservation();
        reservation.setFlight(session.getSelectedFlight());
        reservation.setUser(SessionHandler.getInstance().getCurrentUser());
        reservation.confirm();

        reservationDAO.save(reservation);

        this.bookingReservation = reservation;

        return reservation;
    }

    private void saveSeatReservations(Reservation reservation) {
        for (SeatReservation sr : session.getSeatReservations()) {
            sr.setReservation(reservation);
            sr.confirm();
            seatReservationDAO.save(sr);
        }
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

    public void setJourneyType(String type) {
        session.setJourneyType(type);
    }

    public String getJourneyType(){
        return session.getJourneyType();
    }

    public void setOutwardFlight(Flight selected){
        session.setSelectedFlight(selected);
        session.activateOutwardLeg();
    }

    public void activateOutward() {
        session.activateOutwardLeg();
    }

    public void activateReturn() {
        session.activateReturnLeg();
    }

    public void setReturnFlight(Flight selected) {
        session.activateReturnLeg();
        session.setSelectedFlight(selected);
    }

    public boolean isRoundTrip(){
        return session.isRoundTrip();
    }

    public boolean isOutwardActive() {
        return session.isOutwardLegActive();
    }

    public boolean isReturnActive() {
        return session.isReturnLegActive();
    }
}
