package service.flight;

import domain.flight.*;
import dto.flight.FlightSearchRequest;
import dto.flight.FlightSearchResult;
import dto.flight.SeatAvailability;
import persistence.dao.flight.*;

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
        private final SeatDAO seatDAO = new SeatDAO();

        //metodi
        public List<Flight> getFlightList() {
            List<Flight> flights = flightDAO.findAll();
            setUpMaps();
            objectMapper(flights);
            return flights;
        }

        private void setUpMaps() {
            if(aircraftMap.isEmpty())
                createAircraftMap();
            if(airlineMap.isEmpty())
                createAirlinesMap();
            if(airportsMap.isEmpty())
                createAirportsMap();
        }

        public int getMinPriceAvailable(Long flight_id){
            Optional<Integer> tmpValue = flightDAO.findMinPriceAvailable(flight_id);
            return tmpValue.orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));
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
                for(Flight f : flights){
                    SeatAvailability seatAvailability = seatDAO.getSeatAvailabilityByFlightId(f.getFlightId());
                    if(request.getTotalPassengers() <= seatAvailability.getAvailableSeats())
                        outward.add(f);
                }
                result.setOutwardFlights(outward);
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
            for (Flight flight : flights) {
                objectMapper(flight);
            }
            return flights;
        }

        public Flight objectMapper(Flight flight) {
            setUpMaps();
            Aircraft aircraft = aircraftMap.get(flight.getAircraft().getAircraftId());
            flight.setAircraft(aircraft);

            Airline airline = airlineMap.get(flight.getAirline().getAirlineId());
            flight.setAirline(airline);

            Airport departure = airportsMap.get(flight.getDeparture().getAirportId());
            Airport arrival = airportsMap.get(flight.getArrival().getAirportId());

            flight.setDeparture(departure);
            flight.setArrival(arrival);

            return flight;
        }

        public boolean isAvailable(Flight flight) {
            SeatAvailability seatAvailability = seatDAO.getSeatAvailabilityByFlightId(flight.getFlightId());
            return seatAvailability.getAvailableSeats() != 0;
        }

        private String normalize(String str) {
            return str  == null ? "" : str.trim().toLowerCase();
        } //funzione per normalizzare l'input

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
