package dto.flight;

public class SeatAvailability {
    private int totalSeats;
    private int confirmedSeats;
    private int availableSeats;

    public SeatAvailability(int totalSeats, int confirmedSeats, int availableSeats) {
        this.totalSeats = totalSeats;
        this.confirmedSeats = confirmedSeats;
        this.availableSeats = availableSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getConfirmedSeats() {
        return confirmedSeats;
    }

    public void setConfirmedSeats(int confirmedSeats) {
        this.confirmedSeats = confirmedSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
