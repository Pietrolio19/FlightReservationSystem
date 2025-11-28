package domain.flight;

public class Aircraft {
    private long aircraftId;
    private String model;
    private String producer;
    private int capacity;

    //costruttori
    public Aircraft(){}

    public Aircraft(long aircraftId, String model, String producer, int capacity) {
        this.aircraftId = aircraftId;
        this.model = model;
        this.producer = producer;
        this.capacity = capacity;
    }

    //metodi
    public long getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(long aircraftId) {
        this.aircraftId = aircraftId;
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
