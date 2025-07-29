# Air Traffic Control Simulation

A multithreaded JavaFX-based simulation of an airport's air traffic control system. This project models plane arrivals, servicing, and departures using concurrency and external process integration.

## Features

- Real-time plane simulation using multithreading.
- Integration with external command-line processes for flight and servicing data.
- Interactive JavaFX GUI for monitoring and control.
- Graceful shutdown of threads and resources.
- Configurable parameters for testing and extension.

## Technologies Used

- Java 17+
- JavaFX
- Gradle
- BlockingQueue
- ExecutorService
- ProcessBuilder
- Design Patterns (Strategy, Observer, Template Method)

## System Design

### Thread Architecture

- **Flight Request Listener Thread**: Monitors incoming planes from `saed_flight_requests`.
- **Service Dispatcher Thread**: Sends planes to `saed_plane_service` and tracks responses.
- **Simulation Threads**: Each plane is handled by a separate thread, simulating position, speed, and state.
- **GUI Thread**: Managed by JavaFX for visualization and control buttons.

### Communication and Control

- Uses blocking queues to safely pass data between threads.
- Java `ExecutorService` manages thread pooling.
- Simulation is started/stopped using buttons in the GUI.
- Shutdown is handled via termination signals and cleanup hooks.

## How to Build and Run

1. Open the project in your preferred IDE (such as IntelliJ or Eclipse) or via command line with Gradle.
2. Build the project using Gradle.
3. Run the main application class (`Main.java` or `MainApp.java`) to start the JavaFX interface.
4. Use the **Start** button in the GUI to begin the simulation.
5. Click **End** to stop the simulation gracefully.

Make sure the following executable files are located in `comms/bin/` and have appropriate permissions:

- `saed_flight_requests`
- `saed_plane_service`

## Configuration

Simulation parameters (can be edited directly in code):

| Parameter | Description                    | Default |
|----------:|-------------------------------|--------:|
| `W`       | Map width (in units)           | 10.0    |
| `H`       | Map height (in units)          | 10.0    |
| `N_A`     | Number of airports             | 10      |
| `N_P`     | Number of planes per airport   | 10      |
| `S`       | Plane speed (units per second) | 1.0     |

## Design Patterns Implemented

- **Strategy**: For modular servicing logic per plane.
- **Observer**: GUI components observe plane and system state.
- **Template Method**: Structured flow for simulation cycle.
- **Producer-Consumer**: Queue-based inter-thread coordination.

## License

This simulation project is created for the COMP3003/6007 Software Engineering unit coursework.  
Not intended for commercial distribution or production deployment.
