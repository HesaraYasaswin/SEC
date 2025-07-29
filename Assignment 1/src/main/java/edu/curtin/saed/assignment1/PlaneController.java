package edu.curtin.saed.assignment1;

import java.util.*;
import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PlaneController {

    private static final Logger LOGGER = Logger.getLogger(PlaneController.class.getName());

    private AircraftManager aircraftManager;
    private AirportManager airportManager;
    private boolean running;
    private Statistics statistics;
    private ScheduledExecutorService scheduler;
    private final List<FlightRequestManager> flightRequestManagers;
    private final FlightServicingManager flightServicingManager;

    // Constructor 
    public PlaneController(AirportManager airportManager, Statistics statistics) {
        this.airportManager = airportManager;
        this.statistics = statistics;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(); // Scheduler for periodic tasks
        this.flightRequestManagers = new ArrayList<>();
        this.flightServicingManager = new FlightServicingManager(this::handleServicingRequest);
        this.aircraftManager = new AircraftManager(airportManager, flightServicingManager, statistics);
    }

    // Starts the flight servicing manager in a separate thread
    private void startServicingProcessing() {
        new Thread(flightServicingManager, "FlightServicingManager").start();
    }

    // Sets up the simulation by initializing aircraft movement updates
    public void setupSimulation() {
        aircraftManager.startPlaneMovementUpdates();
        running = true;
        LOGGER.log(Level.INFO, () -> "Simulation setup complete.");
    }

    // Starts the simulation including plane movement and flight request processing
    public void startSimulation() {
        running = true;
        aircraftManager.startPlaneMovementUpdates(); // Start aircraft movement updates

        // Initialize and start flight request managers for each airport
        for (Airport airport : airportManager.getAirports()) {
            FlightRequestManager requestManager = new FlightRequestManager(this::handleFlightRequest, airport.getId());
            flightRequestManagers.add(requestManager);
            new Thread(requestManager, "FlightRequestManager-Airport-" + airport.getId()).start();
        }

        startServicingProcessing(); // Start servicing processing
        LOGGER.log(Level.INFO, () -> "Simulation started.");
    }

    // Ends the simulation by stopping all request managers and servicing processes
    public void endSimulation() {
        running = false;
        for (FlightRequestManager requestManager : flightRequestManagers) {
            requestManager.stop();
        }
        flightServicingManager.stop();
        scheduler.shutdownNow();
        LOGGER.log(Level.INFO, () -> "Simulation ended.");
    }

    // Handles a flight request by assigning an available aircraft to the flight
    private void handleFlightRequest(FlightRequest request) {    
        LOGGER.log(Level.INFO, () -> "Handling flight request from Airport " + request.getOriginAirportId() +
                " to Airport " + request.getDestinationAirportId());

        // Retrieve the origin airport using the airport ID from the flight request
        Airport originAirport = airportManager.getAirportById(request.getOriginAirportId());
        Aircraft availableAircraft = aircraftManager.getAvailableAircraftAtAirport(originAirport); // Get an available aircraft at the origin airport

        if (availableAircraft != null) {
            // Retrieve the destination airport using the destination airport ID from the flight request
            Airport destinationAirport = airportManager.getAirportById(request.getDestinationAirportId()); 

            // Start the flight for the available aircraft, moving it to the destination airport's coordinates
            availableAircraft.startFlight(destinationAirport.getX(), destinationAirport.getY()); 

            LOGGER.log(Level.INFO, () -> String.format("Aircraft %d assigned to flight from Airport %d to Airport %d",
                    availableAircraft.getId(), request.getOriginAirportId(), request.getDestinationAirportId()));

            // Update the simulation in the UI to reflect the new flight assignment
            App.updateSimulation();
        } else {
            LOGGER.log(Level.WARNING, () -> "No available aircraft at origin airport " + request.getOriginAirportId());
        }
    }

    // Handles servicing requests by updating the status of the serviced aircraft
    public void handleServicingRequest(int airportId, int planeId) {     
        // Retrieve the airport where servicing occurred using the airport ID
        Airport airport = airportManager.getAirportById(airportId); 
        if (airport == null) {
            LOGGER.log(Level.WARNING, () -> String.format("Airport with ID %d not found.", airportId));
            return;
        }

        // Retrieve the aircraft that has been serviced using the aircraft ID
        Aircraft servicedAircraft = aircraftManager.getAircraftById(planeId);
        if (servicedAircraft != null) {
            LOGGER.log(Level.INFO, () -> String.format("Aircraft %d completed servicing at Airport %d.", planeId, airportId));

            // Update the aircraft's origin airport and servicing status
            servicedAircraft.setOriginAirport(airport);
            servicedAircraft.setBeingServiced(false);

            // Find the flight request manager for the airport and restart flight requests
            FlightRequestManager requestManager = flightRequestManagers.stream()
                .filter(frm -> frm.getOriginAirportId() == airportId)
                .findFirst()
                .orElse(null);
            if (requestManager != null) {
                requestManager.restartFlightRequests(airportId); // Restart flight requests for the airport
            }

            // Update the simulation in the UI to reflect the servicing completion
            App.updateSimulation();
        } else {
            LOGGER.log(Level.WARNING, () -> String.format("Aircraft with ID %d not found for servicing.", planeId));
        }
    }

    // Returns the list of all aircraft managed by the AircraftManager
    public List<Aircraft> getAircraftList() {
        return aircraftManager.getAircraftList();
    }
}


