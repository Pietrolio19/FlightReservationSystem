package UI.navigator;

import domain.flight.Flight;

public interface Navigator {
    void loadView(String fxmlPath);
    void refreshAuthUI();
    void loadSeatReservationView(Flight flight);
}
