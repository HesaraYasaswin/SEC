package edu.curtin.game;

public class Counter {
    private int itemCount;      
    private int obstacleCount;   

    public Counter() {
        this.itemCount = 0;
        this.obstacleCount = 0;
    }

    public void incrementItemCount() {
        itemCount++;
        
    }

    public void incrementObstacleCount() {
        obstacleCount++;
        
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getObstacleCount() {
        return obstacleCount;
    }

    public void reset() {
        itemCount = 0;
        obstacleCount = 0;
    }
}

