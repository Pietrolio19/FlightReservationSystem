package domain.flight;

enum SeatType {AISLE, MIDDLE, WINDOW}
enum SeatClass {ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST_CLASS}

public class Seat {
    //attributi
    private int seatId;
    private Flight flight;
    private int row;
    private String letter;
    private SeatType type;
    private SeatClass seatClass;
    private int price;

    //costruttore
    public Seat(int seatId, Flight flight, int row, String letter, SeatType type, SeatClass seatClass, int price) {
        this.seatId = seatId;
        this.flight = flight;
        this.row = row;
        this.letter = letter;
        this.type = type;
        this.seatClass = seatClass;
        this.price = price;
    }

    //metodi
    public int getSeatId() {
        return seatId;
    }

    public Flight getFlight() {
        return flight;
    }

    public void  setFlight(Flight flight) {
        this.flight = flight;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public SeatType getType() {
        return type;
    }

    public void setType(SeatType type) {
        this.type = type;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
