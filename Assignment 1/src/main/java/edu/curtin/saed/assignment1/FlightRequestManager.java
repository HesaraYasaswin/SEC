package edu.curtin.saed.assignment1;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



public class FlightRequestManager implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FlightRequestManager.class.getName());
    private static final int THREAD_POOL_SIZE = 10;
    private final FlightRequestHandler requestHandler;
    private int originAirportId;
    private volatile boolean running = true;
    private Process process;
    private final ExecutorService executorService;

    // Interface for handling flight requests
    public interface FlightRequestHandler {
        void handleFlightRequest(FlightRequest request);
    }

    // Constructor
    public FlightRequestManager(FlightRequestHandler requestHandler, int originAirportId) {
        this.requestHandler = requestHandler;
        this.originAirportId = originAirportId;
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE); // Initialize the executor service
    }

    @Override
    public void run() {
        try {
            startFlightRequestProcess(); // Start the process for retrieving flight requests
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, () -> String.format("Error retrieving flight requests for Airport %d: %s", originAirportId, e.getMessage()));
        } finally {
            stopProcess(); // Process is stopped
            shutdownExecutorService(); // Shutdown the executor service
            LOGGER.info(() -> String.format("FlightRequestManager for Airport %d has stopped.", originAirportId));
        }
    }

    // Starts the process to retrieve flight requests and handle them
    private void startFlightRequestProcess() throws IOException {
        String command = getCommand("saed_flight_requests"); // Get the command to execute based on the OS
        process = new ProcessBuilder(command, "10", String.valueOf(originAirportId)).start(); // Start the process

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while (running && (line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                try {
                    int destinationAirportId = Integer.parseInt(line); // Parse the destination airport ID from the line
                    FlightRequest request = new FlightRequest(0, 0, 0, originAirportId, destinationAirportId);
                    executorService.submit(() -> {
                        requestHandler.handleFlightRequest(request); // Handle the flight request
                        LOGGER.info(() -> String.format("Flight request handled from Airport %d to Airport %d", originAirportId, destinationAirportId));
                    });
                } catch (NumberFormatException e) {
                    continue; // Skip lines that do not contain valid integers
                }
            }
        } catch (IOException e) {
            if (!running) {
                LOGGER.info(() -> "Process stopped gracefully.");
                return; // If stopped gracefully, just return
            }
            throw e; // Otherwise, rethrow the exception
        } finally {
            stopProcess(); // Process is stopped even if exceptions occur
        }
    }

    // Restarts the flight request process with a new origin airport ID
    public void restartFlightRequests(int updatedOriginAirportId) {
        this.originAirportId = updatedOriginAirportId;
        stopProcess(); // Stop the current process
        try {
            startFlightRequestProcess(); // Restart the flight request process with the new origin airport ID
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, () -> String.format("Error restarting flight requests for Airport %d: %s", originAirportId, e.getMessage()));
        }
    }

    // Stops the flight request manager and its associated processes
    public void stop() {
        running = false;
        stopProcess();
        shutdownExecutorService();
    }

    private void stopProcess() {
        if (process != null) {
            try {
                process.getOutputStream().close(); // Close output stream
                process.getInputStream().close();  // Close input stream
            } catch (IOException e) {
                LOGGER.warning(() -> "Error closing process streams: " + e.getMessage());
            }
            if (process.isAlive()) {
                process.destroy(); // Destroy the process if it's still alive
                try {
                    if (!process.waitFor(30, TimeUnit.SECONDS)) {
                        process.destroyForcibly(); // Forcefully destroy if not terminated in time
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    process.destroyForcibly(); // Forcefully destroy on interruption
                }
            }
        }
    }

    // Shutdowns the executor service and ensures it's terminated
    private void shutdownExecutorService() {
        if (!executorService.isShutdown()) {
            executorService.shutdown(); // Initiates an orderly shutdown
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

    // Returns the ID of the origin airport
    public int getOriginAirportId() {
        return originAirportId;
    }

    // Determines the command to execute based on the operating system
    private String getCommand(String baseName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return baseName + ".bat"; // For Windows, use .bat file
        } else {
            return baseName; // For other OS, use the base name directly
        }
    }
}


