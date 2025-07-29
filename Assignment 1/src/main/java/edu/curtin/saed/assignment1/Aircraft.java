package edu.curtin.saed.assignment1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Queue;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Aircraft implements Runnable {
    private int id;
    private double x, y;
    private double targetX, targetY;
    private double speed;
    private boolean inFlight;
    private boolean beingServiced;
    private int completedTrips;
    private Airport originAirport;
    private FlightServicingManager flightServicingManager;
    private Statistics statistics;

    private static final double SPEED = 1.0; // Default speed of the aircraft
    private static final Logger LOGGER = Logger.getLogger(Aircraft.class.getName());

    // Constructor
    public Aircraft(int id, Airport airport, FlightServicingManager flightServicingManager, Statistics statistics) {
        this.id = id;
        this.x = airport.getX();
        this.y = airport.getY();
        this.speed = SPEED;
        this.inFlight = false; // Aircraft is not in flight initially
        this.beingServiced = false; // Aircraft is not being serviced initially
        this.completedTrips = 0;
        this.originAirport = airport;
        this.flightServicingManager = flightServicingManager;
        this.statistics = statistics;
    }

    // Start the flight to the target coordinates
    public void startFlight(double targetX, double targetY) {
        synchronized (this) {
            if (beingServiced) {
                return; // Aircraft cannot start a flight if it is being serviced
            }

            this.targetX = targetX;
            this.targetY = targetY;
            this.inFlight = true; // Mark the aircraft as in flight
        }

        // Update statistics
        if (statistics != null) {
            statistics.onAircraftStartedFlight();
        } else {
            LOGGER.log(Level.WARNING, () -> "Statistics is not initialized.");
        }

        LOGGER.log(Level.INFO, () -> String.format("Aircraft %d started flight from Airport %d to (%.2f, %.2f)",
                id, originAirport.getId(), targetX, targetY));
    }

    // Land the aircraft at the destination airport
    public void land(Airport destinationAirport) {
        synchronized (this) {
            this.inFlight = false; // Mark the aircraft as landed
            this.originAirport = destinationAirport;
            this.x = destinationAirport.getX();
            this.y = destinationAirport.getY();

            incrementCompletedTrips();

            // Update statistics
            if (statistics != null) {
                statistics.onAircraftLanded();
            } else {
                LOGGER.log(Level.WARNING, () -> "Statistics is not initialized.");
            }

            // Request servicing if the aircraft is not already being serviced
            if (flightServicingManager != null) {
                if (!beingServiced) {
                    this.beingServiced = true;
                    flightServicingManager.addAircraftForServicing(this);
                }
            } else {
                LOGGER.log(Level.WARNING, () -> "FlightServicingManager is not initialized.");
            }

            LOGGER.log(Level.INFO, () -> String.format("Aircraft %d landed at Airport %d", id, destinationAirport.getId()));
        }
    }

    public boolean hasLanded() {
        synchronized (this) {
            return !inFlight; // Return true if the aircraft is not in flight
        }
    }

    public boolean isBeingServiced() {
        synchronized (this) {
            return beingServiced;
        }
    }

    public void setBeingServiced(boolean beingServiced) {
        synchronized (this) {
            this.beingServiced = beingServiced;
        }
    }

    public boolean isInFlight() {
        synchronized (this) {
            return inFlight;
        }
    }

    public double getX() {
        synchronized (this) {
            return x;
        }
    }

    public double getY() {
        synchronized (this) {
            return y;
        }
    }

    public double getTargetX() {
        synchronized (this) {
            return targetX;
        }
    }

    public double getTargetY() {
        synchronized (this) {
            return targetY;
        }
    }

    public int getId() {
        synchronized (this) {
            return id;
        }
    }

    public double getSpeed() {
        return speed;
    }

    public Airport getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(Airport originAirport) {
        synchronized (this) {
            this.originAirport = originAirport;
        }
    }

    // Increment the number of completed trips
    public void incrementCompletedTrips() {
        synchronized (this) {
            completedTrips++;
            if (statistics != null) {
                statistics.onAircraftCompletedTrip();
            }
        }
    }

    public int getCompletedTripsCount() {
        synchronized (this) {
            return completedTrips;
        }
    }

    public boolean hasReachedDestination() {
        synchronized (this) {
            return !inFlight; // Return true if the aircraft has landed
        }
    }

    // Update the position of the aircraft towards its target destination
    public void updatePosition() {
        synchronized (this) {
            if (!inFlight) {
                return; // No need to update position if not in flight
            }

            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            

            if (distance < speed) {
                x = targetX;
                y = targetY;
                land(originAirport); // Land the aircraft once it reaches the target
                setBeingServiced(false);
            } else {
                double moveX = (dx / distance) * speed;
                double moveY = (dy / distance) * speed;
                x += moveX;
                y += moveY;
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (inFlight) {
                updatePosition(); // Update position if the aircraft is in flight
            }

            try {
                Thread.sleep(100); // Sleep for 100 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // thread interruption
            }
        }
    }
}




