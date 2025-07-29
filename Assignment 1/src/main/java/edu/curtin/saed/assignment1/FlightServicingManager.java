package edu.curtin.saed.assignment1;

import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.logging.*;



public class FlightServicingManager implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FlightServicingManager.class.getName());
    private final FlightServicingHandler servicingHandler; 
    private final ExecutorService executorService; 
    private final List<Aircraft> aircraftForServicing = new ArrayList<>();
    private volatile boolean running = true; 

    // Interface for handling plane servicing
    public interface FlightServicingHandler {
        void handlePlaneServicing(int airportId, int aircraftId);
    }

    // Constructor 
    public FlightServicingManager(FlightServicingHandler servicingHandler) {
        this.servicingHandler = servicingHandler;
        this.executorService = Executors.newCachedThreadPool(); // Use a cached thread pool for servicing tasks
    }

    // Adds an aircraft to the list for servicing if it is not already being serviced or in flight
    public void addAircraftForServicing(Aircraft aircraft) {
        synchronized (this) {
            if (!aircraft.isBeingServiced() && !aircraft.isInFlight()) {
                aircraftForServicing.add(aircraft);
                notifyAll(); // Notify the servicing thread that new aircraft are available
            }
        }
    }

    @Override
    public void run() {
        LOGGER.info(() -> "FlightServicingManager thread started.");
        while (running) {
            Aircraft aircraftToService = null;
            synchronized (this) {
                while (aircraftForServicing.isEmpty() && running) {
                    try {
                        wait(); // Wait for new aircraft to be added
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // interrupted status
                    }
                }
                if (!aircraftForServicing.isEmpty()) {
                    aircraftToService = aircraftForServicing.remove(0); // Remove aircraft from the list for servicing
                }
            }
            if (aircraftToService != null) {
                serviceAircraft(aircraftToService); // Start servicing the aircraft
            }
        }
        LOGGER.info(() -> "FlightServicingManager thread stopped.");
    }

    // Services the given aircraft by submitting a task to the executor service
    private void serviceAircraft(Aircraft aircraft) {
        if (executorService.isShutdown()) {
            LOGGER.warning(() -> "Attempted to service aircraft but ExecutorService is shut down.");
            return;
        }

        int airportId = aircraft.getOriginAirport().getId(); 
        int aircraftId = aircraft.getId(); 

        try {
            executorService.submit(() -> {
                Process proc = null;
                try {
                    LOGGER.info(() -> "Servicing started for Aircraft " + aircraftId + " at Airport " + airportId);

                    // Start the process to service the aircraft
                    proc = new ProcessBuilder(getCommand("saed_plane_service"),
                            String.valueOf(airportId), String.valueOf(aircraftId)).start();

                    // Handle the process's input stream
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                        String servicingOutput = null;
                        String line;
                        while ((line = reader.readLine()) != null) {
                            servicingOutput = line; // Read the output from the process
                        }

                        int exitCode = proc.waitFor(); // Wait for the process to complete

                        if (exitCode == 0 && servicingOutput != null) {
                            LOGGER.info(() -> "Servicing completed for Aircraft " + aircraftId + " at Airport " + airportId);
                            servicingHandler.handlePlaneServicing(airportId, aircraftId); // Notify the handler of completed servicing
                        } else {
                            LOGGER.warning(() -> "Servicing failed for Aircraft " + aircraftId + " at Airport " + airportId);
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    LOGGER.log(Level.SEVERE, () -> "Error during servicing Aircraft " + aircraftId + " at Airport " + airportId + ": " + e.getMessage());
                    Thread.currentThread().interrupt(); // Restore interrupted status
                } finally {
                    if (proc != null) {
                        proc.destroy(); // process is destroyed
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            LOGGER.log(Level.WARNING, () -> "Error submitting servicing task for Aircraft " + aircraftId + " at Airport " + airportId + ": " + e.getMessage());
        }
    }

    // Stops the servicing manager and shuts down the executor service
    public void stop() {
        running = false; // Set running flag to false to stop the servicing loop
        synchronized (this) {
            notifyAll(); // Notify any waiting threads to wake up
        }
        shutdownExecutorService(); 
    }

    // Shuts down the executor service and ensures it is terminated properly
    private void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown(); // Initiate an orderly shutdown
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow(); // Force shutdown if not terminated in time
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow(); // Force shutdown on interruption
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }
    }

    // Determines the command to execute based on the operating system
    private String getCommand(String baseName) {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? baseName + ".bat" : baseName; // Use .bat file for Windows, otherwise use base name
    }
}

