package service;

import domain.flight.Airport;
import domain.flight.Aircraft;
import domain.flight.Airline;
import domain.flight.Flight;
import dto.flight.FlightSearchRequest;
import dto.flight.FlightSearchResult;
import persistence.dao.flight.FlightDAO;
import persistence.dao.flight.AircraftDAO;
import persistence.dao.flight.AirlineDAO;
import persistence.dao.flight.AirportDAO;

import java.util.*;

public class FlightService {
        //attributi
        private final Map<Long, Airport> airportsMap = new HashMap<>();
        private final Map<Long, Aircraft> aircraftMap = new HashMap<>();
        private final Map<Long, Airline> airlineMap = new HashMap<>();
        private final Map<String, Long> airportsByName = new HashMap<>(); //utilizzata per mappatura e filtraggio
        private final FlightDAO flightDAO = new FlightDAO();
        private final AircraftDAO aircraftDAO = new AircraftDAO();
        private final AirlineDAO airlineDAO = new AirlineDAO();
        private final AirportDAO airportDAO = new AirportDAO();

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
                airportsByName.put(normalize(a.getName()), a.getAirportId());
                airportsByName.put(normalize(a.getCity() + " (" + a.getIata() + ")"), a.getAirportId());
                airportsByName.put(normalize(a.getIata()), a.getAirportId());
                airportsByName.put(normalize(a.getName() + " - " + a.getCity() + " (" + a.getIata() + ")"), a.getAirportId());
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

        public List<Airport> airportsFilter(String input) {
            List<Airport> filtered = new ArrayList<>();
            Set<Long> addedIds = new HashSet<>(); //set per evitare i duplicati

            String search = normalize(input);

            for(Map.Entry<String, Long> entry : airportsByName.entrySet()){
                if(matchesKey(entry.getKey(), search) && addedIds.add(entry.getValue())){
                    filtered.add(airportsMap.get(entry.getValue()));
                }
            }
            return filtered;
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

        private String normalize(String str) {
            return str  == null ? "" : str.trim().toLowerCase();
        }

        private boolean matchesKey(String key, String input) {
            String[] parts = key.split("\\s+");
            for (String part : parts) {
                if (part.startsWith(input)) {
                    return true;
                }
            }
            return false;
        } //funzione che controlla il match per parole
}
