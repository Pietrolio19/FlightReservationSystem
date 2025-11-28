package domain.reservation;

import domain.user.Passenger;
import domain.flight.Seat;
import java.time.LocalDate;


public class SeatReservation {
    //attributi
    private long seatReservationId;
    private Passenger passenger;
    private Reservation reservation;
    private Seat seat;
    private LocalDate date = LocalDate.now();
    private ReservationState state = ReservationState.PENDING;

    //costruttori
    public SeatReservation(){}

    public SeatReservation(long seatReservationId, Passenger passenger,
                           Reservation reservation, Seat seat) {
        this.seatReservationId = seatReservationId;
        this.passenger = passenger;
        this.reservation = reservation;
        this.seat = seat;
    }

    //metodi
    public long getSeatReservationId() {
        return seatReservationId;
    }

    public void setSeatReservationId(long seatReservationId) {
        this.seatReservationId = seatReservationId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public void setExpired() {
        if(this.state != ReservationState.PENDING) {
            throw  new IllegalStateException("Solo le prenotaizoni pending possono essere modificate");
        }
        this.state = ReservationState.EXPIRED;
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
}
