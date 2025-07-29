package edu.curtin.game;

public class Goal {
    private int row;
    private int column;
    private boolean visible;

    public Goal(int row, int column) {
        this.row = row;
        this.column = column;
        this.visible = false; 
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isReached(Player player) {
        return player.getRow() == row && player.getColumn() == column;
    }
}

