package domain.reservation;

import domain.user.User;
import domain.flight.Flight;

import java.time.LocalDate;
import java.time.LocalDateTime;

enum ReservationState {PENDING, CONFIRMED, CANCELED}

public class Reservation {
    //attributi
    private Long reservationId;
    private User user;
    private Flight flight;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDate date;
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
    private ReservationState state = ReservationState.PENDING;

    //costruttori
    public Reservation(){}

    public Reservation(Long reservationId, User user, Flight flight) {
        this.reservationId = reservationId;
        this.user = user;
        this.flight = flight;
    }

    //metodi
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getState() {
        return state.toString();
    }

    public void setState(ReservationState state) {
        this.state = state;
    }

    public void setState(String state) {
        if (state == null) {
            this.state = null;
        } else {
            this.state = ReservationState.valueOf(state);
        }
    }

    public void confirm() {
        if(this.state != ReservationState.PENDING) {
            throw new IllegalStateException("Solo le prenotaizoni pending possono essere modificate");
        }
        this.state = ReservationState.CONFIRMED;
    }

    public void cancel() {
        if(this.state != ReservationState.PENDING) {
            throw  new IllegalStateException("Solo le prenotaizoni pending possono essere modificate");
        }
        this.state = ReservationState.CANCELED;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
