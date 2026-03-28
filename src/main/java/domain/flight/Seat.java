package domain.flight;

enum SeatType {AISLE, MIDDLE, WINDOW}
enum SeatClass {ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST_CLASS}

public class Seat {
    //attributi
    private Long seatId;
    private Flight flight;
    private int row;
    private String letter;
    private SeatType type;
    private SeatClass seatClass;
    private int price;

    //costruttori
    public Seat(){}

    public Seat(Long seatId, Flight flight, int row, String letter, int price) { //per test
        this.seatId = seatId;
        this.flight = flight;
        this.row = row;
        this.letter = letter;
        this.price = price;
    }

    public Seat(Long seatId, Flight flight, int row, String letter, SeatType type, SeatClass seatClass, int price) {
        this.seatId = seatId;
        this.flight = flight;
        this.row = row;
        this.letter = letter;
        this.type = type;
        this.seatClass = seatClass;
        this.price = price;
    }

    //metodi
    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
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

    public String getType() {
        return type.toString();
    }

    public void setType(String type) {
        if (type == null) {
            this.type = null;
        } else {
            this.type = SeatType.valueOf(type);
        }
    }

    public String getSeatClass() {
        return seatClass.toString();
    }

    public void setSeatClass(String seatClass) {
        if (seatClass == null) {
            this.seatClass = null;
        } else {
            this.seatClass = SeatClass.valueOf(seatClass);
        }
    }

    public int getPrice() {
        return price;
    }

    public String getFormattedPrice() {return price + "€";}

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSeatCode() {
        return row + letter;
    }
}
