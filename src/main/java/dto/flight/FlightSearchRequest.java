package dto.flight;

import java.time.LocalDate;

public class FlightSearchRequest {
    //attributi
    private String departureAirport;
    private String arrivalAirport;
    private int totalPassengers;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private String journeyType;

    public FlightSearchRequest() {}

    public FlightSearchRequest(String departureAirport, String arrivalAirport, int totalPassengers, LocalDate departureDate,
                               LocalDate returnDate, String journeyType){
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.totalPassengers = totalPassengers;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.journeyType = journeyType;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public int getTotalPassengers() {
        return this.totalPassengers;
    }

    public void setTotalPassengers(int total) {
        this.totalPassengers = total;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getJourneyType() {
        return journeyType;
    }

    public void setJourneyType(String journeyType) {
        this.journeyType = journeyType;
    }
}
