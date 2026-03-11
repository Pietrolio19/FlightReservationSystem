package service;

import domain.flight.Airport;
import domain.flight.Aircraft;
import domain.flight.Airline;
import domain.flight.Flight;
import dto.FlightSearchRequest;
import dto.FlightSearchResult;
import persistence.dao.flight.FlightDAO;
import persistence.dao.flight.AircraftDAO;
import persistence.dao.flight.AirlineDAO;
import persistence.dao.flight.AirportDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class FlightService {
        //attributi
        private Map<Long, Airport> airportsMap = new HashMap<>();
        private Map<Long, Aircraft> aircraftMap = new HashMap<>();
        private Map<Long, Airline> airlineMap = new HashMap<>();
        private Map<String, Long> airportsByName = new HashMap<>();
        private FlightDAO flightDAO = new FlightDAO();
        private AircraftDAO aircraftDAO = new AircraftDAO();
        private AirlineDAO airlineDAO = new AirlineDAO();
        private AirportDAO airportDAO = new AirportDAO();

        //metodi
        public List<Flight> getFlightList() {
            List<Flight> flights = flightDAO.findAll();
            if(aircraftMap.isEmpty())
                createAircraftMap();
            if(airlineMap.isEmpty())
                createAirlinesMap();
            if(airportsMap.isEmpty())
                createAirportsMap();
            objectMapper(flights);
            return flights;
        }

        private void createAirportsMap() {
            List<Airport> airports = airportDAO.findAll();
            for(Airport a: airports) {
                airportsMap.put(a.getAirportId(), a);
                airportsByName.put(a.getName().trim().toLowerCase(), a.getAirportId());
            }
        }

        private void createAircraftMap() {
            List<Aircraft> aircrafts = aircraftDAO.findAll();
            for(Aircraft a: aircrafts) {
                aircraftMap.put(a.getAircraftId(), a);
            }
        }

        private void createAirlinesMap() {
            List<Airline> airlines = airlineDAO.findAll();
            for(Airline a: airlines) {
                airlineMap.put(a.getAirlineId(), a);
            }
        }

        public FlightSearchResult searchFlights(FlightSearchRequest request) {
            Long departureId = airportsByName.get(request.getDepartureAirport());
            Long arrivalId = airportsByName.get(request.getArrivalAirport());
            FlightSearchResult result = new FlightSearchResult();
            List<Flight> outward = new ArrayList<>();
            List<Flight> arrival = new ArrayList<>();

            if(request.getJourneyType().equals("Solo Andata")){
                List<Flight> flights = objectMapper(flightDAO.oneWayFlightSearch(departureId, arrivalId, request.getDepartureDate()));
                result.setOutwardFlights(flights);
                return result;
            }

            List<Flight> twoWay = objectMapper(flightDAO.twoWayFlightSearch(departureId, arrivalId, request.getDepartureDate(), request.getReturnDate()));
            for(Flight f: twoWay){
                if(f.getDeparture().getAirportId().equals(departureId) && f.getArrival().getAirportId().equals(arrivalId)
                        && f.getDepartureDate().equals(request.getDepartureDate()))
                    outward.add(f);

                if(f.getDeparture().getAirportId().equals(arrivalId) && f.getArrival().getAirportId().equals(departureId)
                        && f.getDepartureDate().equals(request.getReturnDate()))
                    arrival.add(f);
            }

            result.setOutwardFlights(outward);
            result.setReturnFlights(arrival);
            return result;
        }

        private List<Flight> objectMapper(List<Flight> flights) {
            for(Flight f: flights) {
                Aircraft aircraft = aircraftMap.get(f.getAircraft().getAircraftId());
                f.setAircraft(aircraft);

                Airline airline = airlineMap.get(f.getAirline().getAirlineId());
                f.setAirline(airline);

                Airport departure = airportsMap.get(f.getDeparture().getAirportId());
                Airport arrival = airportsMap.get(f.getArrival().getAirportId());

                f.setDeparture(departure);
                f.setArrival(arrival);
            }
            return flights;
        }
}
