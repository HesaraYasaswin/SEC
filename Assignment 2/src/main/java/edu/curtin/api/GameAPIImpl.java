package edu.curtin.api;

import edu.curtin.game.Cell;
import edu.curtin.game.Grid;
import edu.curtin.game.Item;
import edu.curtin.game.Player;
import edu.curtin.game.Obstacle;
import edu.curtin.game.Game;
import edu.curtin.game.Counter;
import edu.curtin.game.Goal;

// GameAPIImpl implements the GameAPI interface, providing access to game events and state.
public class GameAPIImpl implements GameAPI {
    private final Player player;
    private final Counter counter;
    private final Game game;
    private final Grid grid;

    public GameAPIImpl(Game game, Player player, Grid grid) {
        this.player = player;
        this.grid = grid;
        this.game = game;
        this.counter = new Counter();
    }

    @Override
    public void onPlayerMove(int newRow, int newColumn) {
        player.move(newRow, newColumn);
    }

    @Override
    public void movePlayer(String direction) {
        game.movePlayer(direction);
    }

    @Override
    public Counter getCounter() {
        return counter;
    }

    @Override
    public void onItemAcquired(Item item) {
        player.addItem(item);
        counter.incrementItemCount();
        System.out.println(getPlayerInventory());
    }

    @Override
    public void onPluginAction() {
        System.out.println("Plugin action triggered.");
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Grid getGrid() {
        return grid;
    }

    @Override
    public int getGridRows() {
        return grid.getRows();
    }

    @Override
    public int getGridColumns() {
        return grid.getColumns();
    }

    @Override
    public int getGoalRow() {
        return grid.getGoal().getRow();
    }

    @Override
    public int getGoalColumn() {
        return grid.getGoal().getColumn();
    }

    @Override
    public Goal getGoal() {
        return grid.getGoal();
    }

    @Override
    public boolean isGoalReached() {
        return grid.isGoalVisibleToPlayer(player) 
                && grid.getCell(player.getRow(), player.getColumn()).isPlayerOnGoal();
    }

    @Override
    public int getItemCount() {
        return counter.getItemCount();
    }

    @Override
    public int getObstacleCount() {
        return counter.getObstacleCount();
    }

    @Override
    public void setPlayerPosition(int row, int column) {
        player.move(row, column);
    }

    @Override
    public boolean isCellVisible(int row, int column) {
        return grid.getCell(row, column).isVisible();
    }

    @Override
    public void setCellVisibility(int row, int column, boolean isVisible) {
        grid.setVisible(row, column);
    }

    @Override
    public Object getCellContents(int row, int column) {
        Cell cell = grid.getCell(row, column);
        if (cell != null) {
            if (cell.hasItem()) {
                return cell.getItem();
            } else if (cell.hasObstacle()) {
                return cell.getObstacle();
            }
            return "Empty Cell";
        }
        return null; 
    }

    @Override
    public String getPlayerInventory() {
        return player.getInventory().displayItems();
    }

    @Override
    public void setObstacle(Obstacle obstacle, int row, int column) {
        grid.setObstacle(obstacle, row, column);
    }

    @Override
    public Item getMostRecentlyAcquiredItem() {
        return player.getInventory().hasAnyItem() ? 
                player.getInventory().getItems().get(player.getInventory().getItems().size() - 1) : null;
    }

    @Override
    public void addItemToInventory(Item item) {
        player.addItem(item);
        onItemAcquired(item);
    }

    @Override
    public String displayGrid() {
        return grid.displayGrid(player);
    }
}

