package domain.flight;

public class Aircraft {
    private int aircraftId;
    private String model;
    private String producer;
    private int capacity;

    //costruttore
    public Aircraft(int aircraftId, String model, String producer, int capacity) {
        this.aircraftId = aircraftId;
        this.model = model;
        this.producer = producer;
        this.capacity = capacity;
    }

    //metodi

    public int getAircraftId() {
        return aircraftId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
