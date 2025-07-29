package edu.curtin.gameplugins;

import edu.curtin.api.GameAPI;
import java.util.Random;

public class Teleport {
    private final GameAPI api;
    private boolean hasTeleported;

    public Teleport(GameAPI api) {
        this.api = api;
        this.hasTeleported = false;
    }

    public void teleport() {
     if (hasTeleported) {
        System.out.println("You can only teleport once.");
        return; // Exit early if teleportation has already occurred
     }


     int rows = api.getGridRows();
     int columns = api.getGridColumns();

     // Generate random coordinates within the grid bounds
     Random random = new Random();
     int newRow = random.nextInt(rows);
     int newColumn = random.nextInt(columns);

     // Set the player's position to the new coordinates
     api.setPlayerPosition(newRow, newColumn);
     System.out.println();
     System.out.println("Player has teleported to (" + newRow + ", " + newColumn + ")");

     // Make neighboring cells visible after teleporting
     setPlayerVisibility(newRow, newColumn);

     // Mark that the teleportation has occurred
     hasTeleported = true;
    }


    private void setPlayerVisibility(int playerRow, int playerColumn) {
        api.setCellVisibility(playerRow, playerColumn, true);

        // Set surrounding cells as visible 
        int[] rowOffsets = {-1, 1, 0, 0};  // Up, down
        int[] colOffsets = {0, 0, -1, 1};  // Left, right

        for (int i = 0; i < 4; i++) {
            int newRow = playerRow + rowOffsets[i];
            int newColumn = playerColumn + colOffsets[i];
            if (newRow >= 0 && newRow < api.getGridRows() && newColumn >= 0 && newColumn < api.getGridColumns()) {
                api.setCellVisibility(newRow, newColumn, true);
            }
        }
    }

    public void onPluginAction() {
        teleport();
    }
}

