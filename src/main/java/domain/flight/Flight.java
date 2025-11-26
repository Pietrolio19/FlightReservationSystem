package domain.flight;

import java.sql.Time;
import java.time.LocalDate;

public class Flight {
    //attributi
    private int flightId;
    private String flightCode;
    private Airport departure;
    private Airport arrival;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private Time  departureTime;
    private Time arrivalTime;
    private int duration; //espressa in minuti
    private Airline airline;
    private Aircraft aircraft;

    //costruttore
    public Flight(int flightId, String flightCode, Airport departure, Airport arrival,
                  LocalDate departureDate, LocalDate arrivalDate, Time departureTime,
                  Time arrivalTime, int duration, Airline airline, Aircraft aircraft) {
        this.flightId = flightId;
        this.flightCode = flightCode;
        this.departure = departure;
        this.arrival = arrival;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.airline = airline;
        this.aircraft = aircraft;
    }

    //metodi
    public int getFlightId() {
        return flightId;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public Airport getDeparture() {
        return departure;
    }

    public void setDeparture(Airport departure) {
        this.departure = departure;
    }

    public Airport getArrival() {
        return arrival;
    }

    public void setArrival(Airport arrival) {
        this.arrival = arrival;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String formattedDuration() {
        int hours = duration / 60;
        int minutes = duration % 60;
        String formattedMinutes = (minutes < 10) ? "0" + minutes: Integer.toString(minutes);
        return hours + "h" + formattedMinutes + "m";
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }
}

