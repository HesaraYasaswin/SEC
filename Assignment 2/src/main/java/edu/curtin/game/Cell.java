package edu.curtin.game;

public class Cell {
    private boolean visible;
    private Item item;
    private Obstacle obstacle;
    private boolean playerOnGoal = false;

    public Cell() {
        this.visible = false; 
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean hasItem() {
        return item != null;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public void setObstacle(Obstacle obstacle) {
        this.obstacle = obstacle;
    }

    public boolean hasObstacle() {
        return obstacle != null;
    }

    public void removeItem() {
        this.item = null;
    }
    
    public boolean isPlayerOnGoal() {
        return playerOnGoal;
    }

    public void setPlayerOnGoal(boolean playerOnGoal) {
        this.playerOnGoal = playerOnGoal;
    }
}

