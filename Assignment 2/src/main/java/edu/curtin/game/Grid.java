package edu.curtin.game;

import java.util.ArrayList;
import java.util.List;

public class Grid {

    private final int rows;
    private final int columns;
    private final Cell[][] cells;
    private final List<Item> items = new ArrayList<>();
    private Goal goal; 

    public Grid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[rows][columns];

       
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                cells[r][c] = new Cell();
            }
        }
    }
    
    
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public void hideAllCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                cells[r][c].setVisible(false); // Set all cells to hidden
            }
        }
    }

    public void setVisible(int row, int column) {
        if (isInBounds(row, column)) {
            cells[row][column].setVisible(true);
        }
    }

    public void addItem(Item item, int row, int column) {
        if (isInBounds(row, column)) {
            cells[row][column].setItem(item);
            items.add(item);
        }
    }

    public void setObstacle(Obstacle obstacle, int row, int column) {
        if (isInBounds(row, column)) {
            cells[row][column].setObstacle(obstacle);
        }
    }

    public Cell getCell(int row, int column) {
        if (isInBounds(row, column)) {
            return cells[row][column];
        }
        return null;
    }

    public boolean isInBounds(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
    
    public Goal getGoal() {
        return goal;
    }

    
    public boolean isGoalVisibleToPlayer(Player player) {
     int playerRow = player.getRow();
     int playerColumn = player.getColumn();
    
     // Check if the goal is set and visible
     if (goal != null && goal.isVisible()) {
        return true;
     }

     // Check the neighboring cells (up, down, left, right) if not already visible
     return (goal != null &&
            ((playerRow == goal.getRow() && Math.abs(playerColumn - goal.getColumn()) == 1) ||  
             (playerColumn == goal.getColumn() && Math.abs(playerRow - goal.getRow()) == 1)));
    }

    public String displayGrid(Player player) {
     StringBuilder gridDisplay = new StringBuilder();

     for (int r = 0; r < rows; r++) {
        for (int c = 0; c < columns; c++) {
            Cell cell = cells[r][c];

            // Show the player's position as "P"
            if (r == player.getRow() && c == player.getColumn()) {
                gridDisplay.append("P ");
            } 
            // Show goal as "G" if it's visible to the player, otherwise hide it
            else if (goal != null && r == goal.getRow() && c == goal.getColumn()) {
                if (isGoalVisibleToPlayer(player)) {
                    gridDisplay.append("G ");
                } else {
                    gridDisplay.append("# "); // Hide the goal if it's not visible
                }
            } 
            // Check if the cell is visible or hidden
            else if (cell.isVisible()) {
                // Show obstacles as "O"
                if (cell.hasObstacle()) {
                    gridDisplay.append("O ");
                } 
                // Show items as "X"
                else if (cell.hasItem()) {
                    gridDisplay.append("X ");
                } 
                // Show empty visible cells as " "
                else {
                    gridDisplay.append("  "); // Show empty cells as a space
                }
            } 
            // Show hidden cells as "#"
            else {
                gridDisplay.append("# ");
            }
        }
        gridDisplay.append("\n");
     }

     return gridDisplay.toString();
    }


}


