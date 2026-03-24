package dto.flight;

import domain.flight.Seat;

public class SeatState {
    private Seat seat;
    private String state;

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
