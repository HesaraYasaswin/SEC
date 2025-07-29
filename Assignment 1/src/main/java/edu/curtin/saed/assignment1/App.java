package edu.curtin.saed.assignment1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.*;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.Timer; 
/**
 * This is demonstration code intended for you to modify. Currently, it sets up a rudimentary
 * Swing GUI with the basic elements required for the assignment.
 *
 * (There is an equivalent JavaFX version of this, if you'd prefer.)
 *
 * You will need to use the GridArea object, and create various GridAreaIcon objects, to represent
 * the on-screen map.
 *
 * Use the startBtn, endBtn, statusText and textArea objects for the other input/output required by
 * the assignment specification.
 *
 * Break this up into multiple methods and/or classes if it seems appropriate. Promote some of the
 * local variables to fields if needed.
 */




public class App {
    private static PlaneController controller;
    private static Statistics statistics;
    private static EventManager eventManager;
    private static AircraftManager aircraftManager;
    private static AirportManager airportManager;
    private static FlightServicingManager flightServicingManager; 
    private static boolean isSimulationRunning = false; 
    private static GridArea gridArea; 
    private static JLabel statusText;
    private static JTextArea textArea;
    private static Timer simulationTimer; // Fixed the fully qualified name issue
    private static List<GridAreaIcon> airportIcons = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::start);
    }

    public static void start() {
        JFrame window = new JFrame("Air Traffic Simulator");

        airportManager = new AirportManager();
        airportManager.setupAirports();         
        statistics = new Statistics(aircraftManager);        
        controller = new PlaneController(airportManager, statistics);
        flightServicingManager = new FlightServicingManager(controller::handleServicingRequest); 
        aircraftManager = new AircraftManager(airportManager, flightServicingManager, statistics);
        eventManager = new EventManager(controller);

        gridArea = new GridArea(10, 10);
        gridArea.setBackground(new Color(0, 0x60, 0));

        initializeAirports();

        JButton startBtn = new JButton("Start");
        JButton endBtn = new JButton("End");

        startBtn.addActionListener(event -> {
            if (!isSimulationRunning) {
                eventManager.handleStart();
                isSimulationRunning = true;
                startSimulation();
            }
        });

        endBtn.addActionListener(event -> {
            if (isSimulationRunning) {
                stopSimulation();
                eventManager.handleEnd();
                isSimulationRunning = false;
            }
        });

        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isSimulationRunning) {
                    stopSimulation();
                    eventManager.handleEnd(); // Gracefully stop the simulation first
                }
                window.dispose(); // Properly dispose of the window
            }
        });

        statusText = new JLabel();
        textArea = new JTextArea();
        textArea.append("Sidebar\n");
        textArea.append("Text\n");

        JToolBar toolbar = new JToolBar();
        toolbar.add(startBtn);
        toolbar.add(endBtn);
        toolbar.addSeparator();
        toolbar.add(statusText);

        JScrollPane scrollingTextArea = new JScrollPane(textArea);
        scrollingTextArea.setBorder(BorderFactory.createEtchedBorder());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridArea, scrollingTextArea);

        Container contentPane = window.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(toolbar, BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);

        window.setPreferredSize(new Dimension(1200, 1000));
        window.pack();
        splitPane.setDividerLocation(0.75);
        window.setVisible(true);

        // Start statistics collection
        statistics.startStatisticsCollection();
    }

    // Initialize airport icons and store them in airportIcons list
    private static void initializeAirports() {
        for (Airport airport : airportManager.getAirports()) {
            GridAreaIcon airportIcon = new GridAreaIcon(
                (int) airport.getX(),
                (int) airport.getY(),
                0.0, // rotation 
                1.0, // scale
                App.class.getClassLoader().getResource("airport.png"),
                "Airport " + airport.getId()
            );
            airportIcons.add(airportIcon); // Store the icon in the airportIcons list
        }
        gridArea.getIcons().addAll(airportIcons); // Add airport icons to the grid area
    }

    // Initialize planes at airports
    private static void initializePlanes() {
        List<Airport> airports = airportManager.getAirports();
        int planeId = 1;

        // Ensure 10 planes are placed at airports
        for (int i = 0; i < 10; i++) {
            Airport airport = airports.get(i % airports.size()); // Cycle through airports
            Aircraft plane = new Aircraft(planeId++, airport, flightServicingManager, statistics);

            aircraftManager.addAircraft(plane); // Add plane to aircraft manager

            double destinationX = airports.get((i + 1) % airports.size()).getX();
            double destinationY = airports.get((i + 1) % airports.size()).getY();
            plane.startFlight(destinationX, destinationY); // Start the flight to a new destination
        }
    }

    private static void startSimulation() {
        controller.setupSimulation();
        controller.startSimulation();

        simulationTimer = new Timer(100, event -> updateSimulation()); // Fixed the fully qualified name issue

        simulationTimer.start();
    }

    public static void updatePlanePositions(List<Aircraft> aircraftList) {
     SwingUtilities.invokeLater(() -> {
        gridArea.getIcons().clear(); // Clear all icons

        // Re-add airport icons
        gridArea.getIcons().addAll(airportIcons);

        // Add plane icons only for planes that are still in flight
        for (Aircraft aircraft : aircraftList) {
            if (aircraft.isInFlight()) { // Check if the plane is still in flight
                GridAreaIcon planeIcon = new GridAreaIcon(
                    (int) aircraft.getX(),
                    (int) aircraft.getY(),
                    0.0, // rotation
                    1.0, // scale
                    App.class.getClassLoader().getResource("plane.png"),
                    "Plane " + aircraft.getId()
                );
                gridArea.getIcons().add(planeIcon);
            }
        }

        gridArea.repaint(); // Repaint the grid to reflect the new positions
     });
    }

    private static void stopSimulation() {
        if (simulationTimer != null) {
            simulationTimer.stop();
            simulationTimer = null;
        }
        controller.endSimulation();
        System.out.println("Simulation ended. All planes should have been serviced and stopped.");
    }

    public static void updateSimulation() {
     SwingUtilities.invokeLater(() -> {
        // Retrieve updated aircraft positions
        List<Aircraft> updatedAircraftList = controller.getAircraftList();

        updatePlanePositions(updatedAircraftList);

        // Update statusText and textArea with new statistics
        statusText.setText(String.format("In Flight: %d, Servicing: %d, Completed Trips: %d",
            statistics.getInFlightCount(),
            statistics.getServicingCount(),
            statistics.getCompletedTripsCount()));

        textArea.append(String.format("In Flight: %d, Servicing: %d, Completed Trips: %d\n",
            statistics.getInFlightCount(),
            statistics.getServicingCount(),
            statistics.getCompletedTripsCount()));
     });
    }
}


