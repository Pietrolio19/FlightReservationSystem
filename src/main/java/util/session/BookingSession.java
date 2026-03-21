package util.session;

import domain.flight.Flight;
import domain.flight.Seat;
import domain.user.Passenger;

import java.util.ArrayList;
import java.util.List;

public class BookingSession {
    private static BookingSession instance;
    private Flight selectedFlight;
    private List<Seat> selectedSeats = new ArrayList<>();
    private List<Passenger> passengers = new ArrayList<>();

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

    public void clear() {
        selectedFlight = null;
        selectedSeats.clear();
        passengers.clear();
    }
}
