package edu.curtin.saed.assignment1;

public class Airport {
    private int id;
    private double x, y;

    public Airport(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
}

