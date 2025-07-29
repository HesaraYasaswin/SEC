package edu.curtin.saed.assignment1;

import java.util.List;
import java.util.concurrent.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections; 
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.*;


public class AircraftManager {

    private static final Logger LOGGER = Logger.getLogger(AircraftManager.class.getName());

    private final List<Aircraft> aircraftList = new ArrayList<>();
    private final AirportManager airportManager;
    private final ExecutorService planeMovementExecutor;
    private final FlightServicingManager flightServicingManager;
    private final Statistics statistics;
    private final Map<Integer, BlockingQueue<FlightRequest>> airportFlightRequestQueues = new HashMap<>();

    // Constructor
    public AircraftManager(AirportManager airportManager, FlightServicingManager flightServicingManager, Statistics statistics) {
        this.airportManager = airportManager;
        this.flightServicingManager = flightServicingManager;
        this.statistics = statistics;
        this.planeMovementExecutor = Executors.newFixedThreadPool(10); // Thread pool for managing aircraft movements
        initializeAircraft(); // Initialize aircraft after construction
    }

    // Initializes aircraft at each airport
    private void initializeAircraft() {
        setupAircraft(); // Move the setup logic to a separate method
    }

    // Sets up aircraft at each airport
    private void setupAircraft() {
        int id = 1;
        int totalPlanes = 10;

        // Distribute them across airports
        for (Airport airport : airportManager.getAirports()) {
            for (int i = 0; i < totalPlanes / airportManager.getAirports().size(); i++) {
                Aircraft aircraft = new Aircraft(id++, airport, flightServicingManager, statistics);
                aircraftList.add(aircraft);
            }
            airportFlightRequestQueues.put(airport.getId(), new LinkedBlockingQueue<>());
        }

        // Handle remaining planes if the total number is not perfectly divisible by the number of airports
        int remainingPlanes = totalPlanes % airportManager.getAirports().size();
        for (int i = 0; i < remainingPlanes; i++) {
            Airport airport = airportManager.getAirports().get(i);
            Aircraft aircraft = new Aircraft(id++, airport, flightServicingManager, statistics);
            aircraftList.add(aircraft);
        }
    }

    // Updates the position of each aircraft in a separate thread
    public void startPlaneMovementUpdates() {
        for (Aircraft aircraft : aircraftList) {
            planeMovementExecutor.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    if (aircraft.isInFlight()) {
                        aircraft.updatePosition(); // Update the aircraft's position if in flight
                    } else {
                        // Handle the aircraft that has landed
                        if (aircraft.hasLanded()) {
                            if (!aircraft.isBeingServiced()) {
                                flightServicingManager.addAircraftForServicing(aircraft);
                            }
                        }

                        // Handle flight requests if the aircraft is not being serviced
                        if (!aircraft.isBeingServiced()) {
                            BlockingQueue<FlightRequest> requestQueue = airportFlightRequestQueues.get(aircraft.getOriginAirport().getId());
                            if (requestQueue != null && !requestQueue.isEmpty()) {
                                FlightRequest nextRequest = requestQueue.poll(); // Get the next flight request
                                if (nextRequest != null) {
                                    handleFlightRequest(nextRequest); // Handle the flight request
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(500); // Sleep to avoid busy-waiting
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Preserve interruption status
                    }
                }
            });
        }
    }

    

    // Handles a flight request by assigning an available aircraft
    public void handleFlightRequest(FlightRequest request) {
        Airport originAirport = airportManager.getAirportById(request.getOriginAirportId()); // Retrieve the origin airport based on the origin airport ID from the request

        // Get an available aircraft at the origin airport
        Aircraft availableAircraft = getAvailableAircraftAtAirport(originAirport);

        if (availableAircraft != null && !availableAircraft.isBeingServiced()) {
            Airport destinationAirport = airportManager.getAirportById(request.getDestinationAirportId()); // Retrieve the destination airport based on the destination airport ID from the request
            availableAircraft.startFlight(destinationAirport.getX(), destinationAirport.getY()); // Start the flight for the available aircraft, moving it to the destination airport's coordinates

            // Log
            LOGGER.log(Level.INFO, () -> String.format("Assigned Aircraft %d to flight from Airport %d to Airport %d",
                    availableAircraft.getId(), request.getOriginAirportId(), request.getDestinationAirportId()));
        } else {
            LOGGER.log(Level.WARNING, () -> String.format("No available aircraft at Airport %d. The request is added to the queue.",
                    originAirport.getId()));

            // Add the flight request to the queue for the origin airport if no aircraft is available
            airportFlightRequestQueues.get(request.getOriginAirportId()).offer(request);
        }
    }

    // Returns an available aircraft at the specified airport
    public Aircraft getAvailableAircraftAtAirport(Airport airport) {
        return aircraftList.stream()
                .filter(aircraft -> aircraft.getOriginAirport().equals(airport) && !aircraft.isInFlight())
                .findFirst()
                .orElse(null);
    }

    // Finds an aircraft by its ID
    public Aircraft getAircraftById(int aircraftId) {
        return aircraftList.stream()
                .filter(aircraft -> aircraft.getId() == aircraftId)
                .findFirst()
                .orElse(null);
    }
    
    
    // Shuts down the plane movement executor service
    public void shutdown() {
        planeMovementExecutor.shutdown();
        try {
            if (!planeMovementExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                planeMovementExecutor.shutdownNow(); // Force shutdown if not terminated within the timeout
            }
        } catch (InterruptedException e) {
            planeMovementExecutor.shutdownNow(); // Force shutdown on interruption
            Thread.currentThread().interrupt(); // Preserve interruption status
        }
    }

    // Returns the list of all aircraft managed by this manager
    public List<Aircraft> getAircraftList() {
        return aircraftList;
    }

    // Adds a new aircraft to the list
    public void addAircraft(Aircraft aircraft) {
        aircraftList.add(aircraft);
    }
}

