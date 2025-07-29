package edu.curtin.gameplugins;

import edu.curtin.api.GameAPI;
import edu.curtin.game.Item;
import edu.curtin.game.Obstacle;
import edu.curtin.Localization;

import java.util.Locale;


public class Reveal {

    private final GameAPI gameAPI;
    private boolean hasRevealed; 
    private Localization localization; 

    public Reveal(GameAPI gameAPI) {
        this.gameAPI = gameAPI;
        this.hasRevealed = false;
        this.localization = new Localization();
    }

    
    public void onPluginAction() {
     if (hasRevealed) {
        System.out.println(localization.getMessage("reveal.already.revealed"));
        return; // Exit early since the action has already been done
     }

     Item mostRecentItem = gameAPI.getMostRecentlyAcquiredItem();
    
     if (mostRecentItem != null && mostRecentItem.getName().toLowerCase().contains("yellow shield")) {  // so basically it works only if the item is yellow shield
        revealGoalAndItemsAndObstacles();
        hasRevealed = true;  // Mark that the reveal action has been completed
     }
    }


    
    private void revealGoalAndItemsAndObstacles() {
        int rows = gameAPI.getGridRows();
        int columns = gameAPI.getGridColumns();

        // Reveal the goal location as well
        int goalRow = gameAPI.getGoalRow();
        int goalColumn = gameAPI.getGoalColumn();
        gameAPI.setCellVisibility(goalRow, goalColumn, true);
        gameAPI.getGoal().setVisible(true);

        // Print to check if goal is set correctly
        System.out.println();
        System.out.println(localization.getMessage("reveal.goal") + ": (" + goalRow + ", " + goalColumn + ")");

        // Reveal all remaining hidden items and obstacles
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Check for hidden items
                if (gameAPI.getCellContents(row, col) instanceof Item && !gameAPI.isCellVisible(row, col)) {
                    gameAPI.setCellVisibility(row, col, true);  // Reveal the cell with the item
                }

                // Check for hidden obstacles
                if (gameAPI.getCellContents(row, col) instanceof Obstacle && !gameAPI.isCellVisible(row, col)) {
                    gameAPI.setCellVisibility(row, col, true);  // Reveal the cell with the obstacle
                }
            }
        }

        System.out.println(localization.getMessage("reveal.success"));
    }
}

