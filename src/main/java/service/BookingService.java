package service;

import UI.controller.flight.SeatReservationController;
import domain.flight.Seat;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import persistence.dao.flight.SeatDAO;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.reservation.SeatReservationDAO;
import persistence.dao.user.PassengerDAO;
import util.session.BookingSession;

import java.util.ArrayList;
import java.util.Map;

public class BookingService {
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final SeatReservationDAO seatReservationDAO = new SeatReservationDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    BookingSession session = BookingSession.getInstance();

    public void savePassengers(Map<String, Passenger> passengerMap) {
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
}
