package edu.curtin.game;

import java.util.HashSet;
import java.util.Set;

public class Player {
    private int row;
    private int column;
    private final Inventory inventory = new Inventory();

    public Player(int startRow, int startColumn) {
        this.row = startRow;
        this.column = startColumn;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void move(int newRow, int newColumn) {
        this.row = newRow;
        this.column = newColumn;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        inventory.addItem(item);
    }

    public boolean hasItem(String itemName) {
        return inventory.hasItem(itemName);
    }
}

