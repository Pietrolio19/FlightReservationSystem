package domain.service;

import domain.flight.Airport;
import domain.flight.Aircraft;
import domain.flight.Airline;
import domain.flight.Flight;
import persistence.dao.flight.FlightDAO;
import persistence.dao.flight.AircraftDAO;
import persistence.dao.flight.AirlineDAO;
import persistence.dao.flight.AirportDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class FlightService {
        //attributi
        private Map<Long, Airport> airportsMap = new HashMap<>();
        private Map<Long, Aircraft> aircraftMap = new HashMap<>();
        private Map<Long, Airline> airlineMap = new HashMap<>();
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

        private void createAirportsMap() {
            List<Airport> airports = airportDAO.findAll();
            for(Airport a: airports) {
                airportsMap.put(a.getAirportId(), a);
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

}
