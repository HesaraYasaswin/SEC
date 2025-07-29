package edu.curtin.saed.assignment1;

import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class AirportManager {
    private List<Airport> airports; 
    private static final double MIN_DISTANCE = 1.0; // Minimum distance allowed between airports

    // Constructor 
    public AirportManager() {
        airports = new ArrayList<>(); 
    }

    // Method to set up airports with random coordinates ensuring they are not too close to each other
    public void setupAirports() {
        Random random = new Random(); // Random number generator for airport coordinates
        for (int i = 0; i < 10; i++) { // Create 10 airports
            double x, y; // Coordinates for the new airport
            boolean tooClose; 
            do {
                x = random.nextDouble() * 10; 
                y = random.nextDouble() * 10; 
                tooClose = isTooCloseToOtherAirports(x, y); // Check if the new airport is too close to others
            } while (tooClose); // Repeat until a valid location is found

            Airport airport = new Airport(i, x, y); // Create a new Airport object
            airports.add(airport); // Add the new airport to the list
        }
        System.out.println("Airports initialized: " + airports.size()); // Print the number of airports initialized
    }

    // to check if a proposed airport location is too close to any existing airport
    private boolean isTooCloseToOtherAirports(double x, double y) {
        for (Airport airport : airports) {
            // Calculate the distance between the new location and the current airport
            double distance = Math.sqrt(Math.pow(airport.getX() - x, 2) + Math.pow(airport.getY() - y, 2));
            if (distance < MIN_DISTANCE) {
                return true; // Return true if the new airport is too close to an existing one
            }
        }
        return false; // Return false if the new airport is sufficiently far from all existing ones
    }

    public List<Airport> getAirports() {
        return airports; 
    }

    // Method to find an airport by its ID
    public Airport getAirportById(int id) {
        return airports.stream() // Stream through the list of airports
            .filter(airport -> airport.getId() == id) // Filter airports by ID
            .findFirst() // Get the first match if it exists
            .orElse(null); // Return null if no airport is found with the given ID
    }
    
    // Method to find an airport by its coordinates
    public Airport getAirportByCoordinates(double x, double y) {
        return airports.stream() 
            .filter(airport -> airport.getX() == x && airport.getY() == y) 
            .findFirst() 
            .orElse(null); // Return null if no airport is found at the given coordinates
    }
}


