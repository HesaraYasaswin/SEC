package edu.curtin.gameplugins;

import edu.curtin.api.GameAPI;
import edu.curtin.game.Item;

public class Prize {
    private GameAPI gameAPI;
    private String specialItemName;

    public Prize(GameAPI gameAPI) {
        this.gameAPI = gameAPI;
        this.specialItemName = "Mythic"; // Special item name
    }

    public void onPluginAction() {
        System.out.println("Plugin action triggered.");
        checkForPrize();
    }

    private void checkForPrize() {
        // Check the player's inventory size
        int inventorySize = gameAPI.getPlayer().getInventory().getItems().size();
        System.out.println("Current inventory size: " + inventorySize);

        // Award the prize if the inventory size reaches 5
        if (inventorySize >= 5 && !gameAPI.getPlayer().hasItem(specialItemName)) {
            System.out.println("Inventory size is 5 or more, adding special item...");
            addSpecialItem();
        } else {
            System.out.println("");
        }
    }

    private void addSpecialItem() {
        Item specialItem = new Item(specialItemName);
        gameAPI.getPlayer().addItem(specialItem); // add it
        System.out.println("Added special item to inventory: " + specialItem.getName());
        System.out.println("Updated Inventory: " + gameAPI.getPlayer().getInventory().displayItems());
    }
}

