package edu.curtin.game;

import edu.curtin.InputParser;
import edu.curtin.Localization;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.text.Normalizer;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;
import java.io.IOException;  
import java.text.ParseException; 

public class Game {

    private Grid grid;
    private Player player;
    private Goal goal;
    private int moves;
    private Counter counter;
    private final InputParser inputParser;
    private LocalDate gameDate;  
    private DateTimeFormatter dateFormatter;  
    private Localization localization; 

    public Game(String inputFilePath, Locale locale) {
        this.inputParser = new InputParser(inputFilePath, this);
        this.moves = 0;
        this.gameDate = LocalDate.now();  
        this.counter = new Counter();
        this.localization = new Localization(); 
        this.dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale); 
    }

    public void setup() throws IOException, ParseException {
        inputParser.parseInput();  // Parse the input file 
    }

    
    public void updateLocale(Locale locale) {
      this.dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale);
    }

    public void initializeGrid(int rows, int columns) {
        this.grid = new Grid(rows + 1, columns + 1);  // Initialize the grid (+1 was added to each cuz the generated input files sometimes starts from 0)
    }

    public void initializePlayer(int startRow, int startColumn) {
        this.player = new Player(startRow, startColumn);  // player's starting position
        setPlayerVisibility();  
    }

    public void initializeGoal(int goalRow, int goalColumn) {
        this.goal = new Goal(goalRow, goalColumn);  // goal at the specified position
        grid.setGoal(goal);  
    }

    public void setPlayerVisibility() {
        int playerRow = player.getRow();
        int playerColumn = player.getColumn();

        // Set the player's cell as visible
        grid.setVisible(playerRow, playerColumn);

        // Set surrounding cells as visible(neighbouring cells)
        int[] rowOffsets = {-1, 1, 0, 0}; // Up, down
        int[] colOffsets = {0, 0, -1, 1}; // Left, right

        for (int i = 0; i < 4; i++) {
            int newRow = playerRow + rowOffsets[i];
            int newColumn = playerColumn + colOffsets[i];
            grid.setVisible(newRow, newColumn);
        }
    }

    public void addItem(Item item, int row, int column) {
        grid.addItem(item, row, column);  
    }

    public void setObstacle(Obstacle obstacle, int row, int column) {
        grid.setObstacle(obstacle, row, column);  
    }

    public void movePlayer(String direction) { //press the key enter after each movement entered.
        int newRow = player.getRow();
        int newColumn = player.getColumn();

        switch (direction) {
            case "w":  // up
                newRow--;
                break;
            case "s":  // down
                newRow++;
                break;
            case "a":  // left
                newColumn--;
                break;
            case "d":  // right
                newColumn++;
                break;
            default:
                return; // Invalid 
        }

        if (grid.isInBounds(newRow, newColumn)) {
            Cell nextCell = grid.getCell(newRow, newColumn);

            // Handling goal reaching
            if (goal != null && newRow == goal.getRow() && newColumn == goal.getColumn()) {
                player.move(newRow, newColumn);  // Move the player to the goal
                moves++;  // Increment the move counter
                gameDate = gameDate.plusDays(1);  // Advance the in-game date by 1 day
                setPlayerVisibility();  // Update visibility 
                System.out.println(displayGrid());
                System.out.println(localization.getMessage("game.reach.goal")); // the message isYou have reached the goal

            } else if (nextCell.hasObstacle()) {
                // If there's an obstacle, check if the player can pass
                if (nextCell.getObstacle().canPass(player)) {
                    player.move(newRow, newColumn);  // Move the player
                    moves++;  
                    gameDate = gameDate.plusDays(1);  
                    setPlayerVisibility();  

                    counter.incrementObstacleCount(); // Increment obstacle count
                } else {
                    System.out.println(localization.getMessage("game.cannot.pass.obstacle")); // the message is Cannot pass the obstacle! You need the required item.
                    return;
                }
            } else {
                // Move to any non-obstacle cell
                player.move(newRow, newColumn);  // Move the player
                moves++;  
                gameDate = gameDate.plusDays(1);  
                setPlayerVisibility();  
            }

            // Check if there's an item in the new cell and pick it up
            if (nextCell.hasItem()) {
                player.addItem(nextCell.getItem());
                counter.incrementItemCount(); // Increment item count
                nextCell.removeItem();  // Remove the item after pickup
            }
        } else {
            System.out.println(localization.getMessage("game.move.out.of.bounds")); // message is Move out of bounds!
        }

        // Display the updated date
        System.out.println(localization.getMessage("game.current.date") + ": " + gameDate.format(dateFormatter)); 
    }

    public boolean isGameOver() {
        return goal != null && goal.isReached(player);  // to end
    }

    public String displayGrid() {
        return grid.displayGrid(player);  
    }

    public String displayInventory() {
        return player.getInventory().displayItems();  
    }

    public int getMoves() {
        return moves;  // Return the total number of moves made by the player
    }

    public LocalDate getGameDate() {
        return gameDate;  // Return the current in-game date
    }

    public Player getPlayer() {
        return player;
    }

    public Grid getGrid() {
        return grid;
    }

    public Item getItemByName(String itemName) {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                Cell cell = grid.getCell(row, col);
                if (cell.hasItem() && cell.getItem().getName().equals(itemName)) {
                    return cell.getItem();
                }
            }
        }
        return null; 
    }
}

