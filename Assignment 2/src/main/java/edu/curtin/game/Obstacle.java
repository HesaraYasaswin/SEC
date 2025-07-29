package edu.curtin.game;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

public class Obstacle {
    private final Set<String> requiredItems = new HashSet<>();

    public void addRequiredItem(String itemName) {
        // Normalize the item name before adding it to the set
        String normalizedItemName = Normalizer.normalize(itemName, Normalizer.Form.NFKC);
        requiredItems.add(normalizedItemName);
    }

    public boolean canPass(Player player) {
        // If the obstacle requires specific items, check if the player has them
        for (String requiredItem : requiredItems) {
            if (!player.getInventory().hasItem(requiredItem)) {
                return false;  // does not have it
            }
        }
        return true;  // Player has all required items
    }

    public Set<String> getRequiredItems() {
        return requiredItems;
    }
}


