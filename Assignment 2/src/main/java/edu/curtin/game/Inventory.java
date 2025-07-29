package edu.curtin.game;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean hasAnyItem() {
        return !items.isEmpty();
    }
    
    public List<Item> getItems() { 
        return items;
    }

    public boolean hasItem(String itemName) {
        for (Item item : items) {
            // Normalize both strings to the same form for comparison
            String normalizedItemName = Normalizer.normalize(item.getName(), Normalizer.Form.NFKC);
            String normalizedInput = Normalizer.normalize(itemName, Normalizer.Form.NFKC);

            if (normalizedItemName.equals(normalizedInput)) {
                return true;  // The player has the required item
            }
        }
        return false;  
    }

    public String displayItems() {
        if (items.isEmpty()) {
            return "Inventory is empty.";
        }
        StringBuilder itemList = new StringBuilder("Inventory: ");
        for (Item item : items) {
            itemList.append(item.getName()).append(", ");
        }
        return itemList.substring(0, itemList.length() - 2); // Remove the last comma and space
    }
}

