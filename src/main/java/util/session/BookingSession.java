package util.session;

import domain.flight.Flight;
import domain.flight.Seat;
import domain.reservation.SeatReservation;
import domain.user.Passenger;

import java.util.*;

public class BookingSession {
    private static BookingSession instance;
    private Flight selectedFlight;
    private int totalPassengers;
    private List<Seat> selectedSeats = new ArrayList<>();
    private List<Passenger> passengers = new ArrayList<>();
    private List<SeatReservation> seatReservations = new ArrayList<>();
    private final Map<String, Passenger> passengerBySeatCode = new LinkedHashMap<>();

    public static BookingSession getInstance() {
        if(instance == null)
            instance = new BookingSession();
        return instance;
    }

    public Flight getSelectedFlight() {
        return selectedFlight;
    }

    public void setSelectedFlight(Flight selectedFlight) {
        this.selectedFlight = selectedFlight;
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<Seat> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    public int getTotalPassengers() {
        return totalPassengers;
    }

    public void setTotalPassengers(int totalPassengers) {
        this.totalPassengers = totalPassengers;
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

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public void addPassenger(Passenger passenger) {
        this.passengers.add(passenger);
    }

    public void removePassenger(Passenger passenger) {
        this.passengers.remove(passenger);
    }

    public void clearPassengers() {
        this.passengers.clear();
    }

    public List<SeatReservation> getSeatReservations() {
        return seatReservations;
    }

    public void setSeatReservations(List<SeatReservation> seatReservations) {
        this.seatReservations = seatReservations;
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
        selectedFlight = null;
        selectedSeats.clear();
        passengers.clear();
    }
}
