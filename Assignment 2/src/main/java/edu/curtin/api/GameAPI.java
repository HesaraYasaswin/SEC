package edu.curtin.api;

import edu.curtin.game.Item;
import edu.curtin.game.Player;
import edu.curtin.game.Grid;
import edu.curtin.game.Obstacle;
import edu.curtin.game.Game;
import edu.curtin.game.Counter;
import edu.curtin.game.Goal;

public interface GameAPI 
{
    void onPlayerMove(int newRow, int newColumn);  // Callback mechanism when the player moves
    
    void onItemAcquired(Item item);  // Callback mechanism when the player acquires a new item
    
    void onPluginAction();  // Callback mechanism when the plugin/script's menu option is selected
    
    Player getPlayer();  // to get the current player instance    
    Grid getGrid();      
    int getGridRows();  // to get the number of rows in the grid    
    int getGridColumns();  // to get the number of columns in the grid    
    boolean isGoalReached();  // to check if the player has reached the goal    
    void setPlayerPosition(int row, int column);  // to set the player's position in the grid    
    boolean isCellVisible(int row, int column);      
    void setCellVisibility(int row, int column, boolean isVisible);      
    Object getCellContents(int row, int column);  // get the contents of a specific cell    
    String getPlayerInventory();  // Method to get the player's inventory    
    Item getMostRecentlyAcquiredItem();  // to get the most recently acquired item    
    void addItemToInventory(Item item);      
    void setObstacle(Obstacle obstacle, int row, int column);  // Method to set an obstacle at a specific grid location    
    String displayGrid();     
    void movePlayer(String direction);  // Method to move the player in a specific direction    
    Counter getCounter();  // Method to get the counter instance    
    int getGoalRow();      
    int getGoalColumn();      
    Goal getGoal();      
    int getItemCount();      
    int getObstacleCount(); 
}

