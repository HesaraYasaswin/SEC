package edu.curtin.saed.assignment1;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;


public class Statistics {

    private final AtomicInteger inFlightCount = new AtomicInteger(0);
    private final AtomicInteger servicingCount = new AtomicInteger(0);
    private final AtomicInteger completedTripsCount = new AtomicInteger(0);

    private final AircraftManager aircraftManager;
    private final ScheduledExecutorService scheduler;

    // Constructor
    public Statistics(AircraftManager aircraftManager) {
        this.aircraftManager = aircraftManager;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        resetStatistics();
    }

    // Returns the number of aircraft currently in-flight
    public int getInFlightCount() {
        return inFlightCount.get();
    }

    // Returns the number of aircraft currently being serviced
    public int getServicingCount() {
        return servicingCount.get();
    }

    // Returns the number of completed trips
    public int getCompletedTripsCount() {
        return completedTripsCount.get();
    }

    // Updates the statistics based on the current state of the aircraft
    public void updateStatistics() {
        // for number of in-flight aircraft
        inFlightCount.set((int) aircraftManager.getAircraftList().stream()
            .filter(Aircraft::isInFlight)
            .count());

        // for total number of completed trips
        completedTripsCount.set(aircraftManager.getAircraftList().stream()
            .mapToInt(Aircraft::getCompletedTripsCount)
            .sum());
    }

    // Starts collecting statistics at a fixed rate
    public void startStatisticsCollection() {
        // Schedule the updateStatistics method to run every second
        scheduler.scheduleAtFixedRate(() -> {
            updateStatistics(); // Update statistics
        }, 0, 1, TimeUnit.SECONDS);
    }

    // Stops collecting statistics and shuts down the scheduler
    public void stopStatisticsCollection() {
        scheduler.shutdown();
        try {
            // Wait for up to 1 second for the scheduler to terminate
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // Force shutdown if not terminated in time
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            scheduler.shutdownNow(); // Force shutdown if interrupted
        }
    }

    // Increment the count of servicing aircraft when servicing starts
    public void onAircraftServicingStarted() {
        servicingCount.incrementAndGet();
    }

    // Decrement the count of servicing aircraft when servicing completes
    public void onAircraftServicingCompleted() {
        servicingCount.decrementAndGet();
    }

    // Increment the count of in-flight aircraft when an aircraft starts flight
    public void onAircraftStartedFlight() {
        inFlightCount.incrementAndGet();
    }

    // Update counts when an aircraft lands
    public void onAircraftLanded() {
        inFlightCount.decrementAndGet();
        completedTripsCount.incrementAndGet();
    }

    // Increment the count of completed trips
    public void onAircraftCompletedTrip() {
        completedTripsCount.incrementAndGet();
    }

    // Reset all statistics counters to zero
    private void resetStatistics() {
        inFlightCount.set(0);
        servicingCount.set(0);
        completedTripsCount.set(0);
    }
}

