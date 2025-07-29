package edu.curtin.saed.assignment1;

public class EventManager {
    private PlaneController controller;

    public EventManager(PlaneController controller) {
        this.controller = controller;
    }

    public void handleStart() {
        controller.startSimulation();
    }

    public void handleEnd() {
        controller.endSimulation();
    }

    public void handleWindowClose() {
        controller.endSimulation();
    }
    
}


