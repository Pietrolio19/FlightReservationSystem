package util.session;

import domain.flight.Flight;
import domain.flight.Seat;
import domain.reservation.SeatReservation;
import domain.user.Passenger;

import java.util.*;

enum LegType {OUTWARD, RETURN}

public class BookingSession {
    private static BookingSession instance;
    private int totalPassengers = 1;
    private String journeyType;
    private final BookingLeg outwardLeg = new BookingLeg();
    private final BookingLeg returnLeg = new BookingLeg();
    private List<Passenger> passengers = new ArrayList<>();
    private LegType activeLeg = LegType.OUTWARD;

    public static BookingSession getInstance() {
        if(instance == null)
            instance = new BookingSession();
        return instance;
    }

    public int getTotalPassengers() {
        return totalPassengers;
    }

    public void setTotalPassengers(int totalPassengers) {
        this.totalPassengers = totalPassengers;
    }

    public void setJourneyType(String type) {
        journeyType = type;
    }

    public String getJourneyType() {
        return journeyType;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers.clear();
        if (passengers != null) {
            this.passengers.addAll(passengers);
        }
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

    public void clearTotal(){
        totalPassengers = 1;
    }

    public void clear() {
        journeyType = null;
        activeLeg = LegType.OUTWARD;

        passengers.clear();
        outwardLeg.clear();
        returnLeg.clear();
    }

    public void activateOutwardLeg() {
        this.activeLeg = LegType.OUTWARD;
    }

    public void activateReturnLeg() {
        this.activeLeg = LegType.RETURN;
    }

    private LegType getActiveLegType() {
        return activeLeg;
    }

    public BookingLeg getActiveLeg() {
        if (activeLeg == LegType.OUTWARD) {
            return outwardLeg;
        }
        return returnLeg;
    }

    public Flight getSelectedFlight() {
        return getActiveLeg().currentFlight();
    }

    public void setSelectedFlight(Flight flight) {
        getActiveLeg().setSelectedFlight(flight);
    }

    public List<Seat> getSelectedSeats() {
        return getActiveLeg().getSelectedSeats();
    }

    public void setSelectedSeats(List<Seat> selectedSeats) {
        getActiveLeg().setSelectedSeats(selectedSeats);
    }

    public void addSeat(Seat seat) {
        getActiveLeg().addSeat(seat);
    }

    public void removeSeat(Seat seat) {
        getActiveLeg().removeSeat(seat);
    }

    public void clearSeats() {
        getActiveLeg().clearSeats();
    }

    public List<SeatReservation> getSeatReservations() {
        return getActiveLeg().getSeatReservations();
    }

    public void setSeatReservations(List<SeatReservation> seatReservations) {
        getActiveLeg().setSeatReservations(seatReservations);
    }

    public void addSeatReservation(SeatReservation reservation) {
        getActiveLeg().addSeatReservation(reservation);
    }

    public void removeSeatReservation(SeatReservation reservation) {
        getActiveLeg().removeSeatReservation(reservation);
    }

    public void clearSeatReservations() {
        getActiveLeg().clearSeatReservations();
    }

    public void addMappedElement(String seatCode, Passenger passenger) {
        getActiveLeg().addMappedElement(seatCode, passenger);
    }

    public Map<String, Passenger> getPassengerBySeatCode() {
        return getActiveLeg().getPassengerBySeatCode();
    }

    public boolean isRoundTrip() {
        return "Andata e Ritorno".equals(journeyType);
    }

    public boolean isOutwardLegActive() {
        return activeLeg == LegType.OUTWARD;
    }

    public boolean isReturnLegActive() {
        return activeLeg == LegType.RETURN;
    }
}
