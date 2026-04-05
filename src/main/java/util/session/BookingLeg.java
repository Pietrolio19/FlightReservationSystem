package util.session;

import domain.flight.Flight;
import domain.flight.Seat;
import domain.reservation.SeatReservation;
import domain.user.Passenger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingLeg {
    private Flight selectedFlight;
    private List<Seat> selectedSeats = new ArrayList<>();
    private List<SeatReservation> seatReservations = new ArrayList<>();
    private final Map<String, Passenger> passengerBySeatCode = new LinkedHashMap<>();

    public Flight currentFlight() {
        return selectedFlight;
    }

    public void setSelectedFlight(Flight selectedFlight) {
        this.selectedFlight = selectedFlight;
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<Seat> selectedSeats) {
        this.selectedSeats.clear();
        if (selectedSeats != null) {
            this.selectedSeats.addAll(selectedSeats);
        }
    }

    public void addSeat(Seat seat){
        this.selectedSeats.add(seat);
    }

    public void removeSeat(Seat seat) {
        this.selectedSeats.remove(seat);
    }

    public void clearSeats() {
        this.selectedSeats.clear();
    }

    public List<SeatReservation> getSeatReservations() {
        return seatReservations;
    }

    public void setSeatReservations(List<SeatReservation> seatReservations) {
        this.seatReservations.clear();
        if (seatReservations != null) {
            this.seatReservations.addAll(seatReservations);
        }
    }

    public void addSeatReservation(SeatReservation reservation){
        this.seatReservations.add(reservation);
    }

    public void removeSeatReservation(SeatReservation reservation) {
        this.seatReservations.remove(reservation);
    }

    public void clearSeatReservations() {
        this.seatReservations.clear();
    }

    public void addMappedElement(String seatCode, Passenger passenger){
        passengerBySeatCode.put(seatCode, passenger);
    }

    public Map<String, Passenger> getPassengerBySeatCode() {
        return passengerBySeatCode;
    }

    public void clear() {
        passengerBySeatCode.clear();
        selectedFlight = null;
        selectedSeats.clear();
        seatReservations.clear();
    }
}
