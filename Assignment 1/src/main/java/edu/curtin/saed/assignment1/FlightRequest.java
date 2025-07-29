package edu.curtin.saed.assignment1;

public class FlightRequest {

    private int aircraftId;
    private double destinationX;
    private double destinationY;
    private int originAirportId;         
    private int destinationAirportId;

    // Constructor
    public FlightRequest(int aircraftId, double destinationX, double destinationY, int originAirportId, int destinationAirportId) {
        this.aircraftId = aircraftId;
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
    }
    
    public int getOriginAirportId() {
        return originAirportId;
    }

    public int getDestinationAirportId() {
        return destinationAirportId;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public double getDestinationX() {
        return destinationX;
    }

    public double getDestinationY() {
        return destinationY;
    }
}

