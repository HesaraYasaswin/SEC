package edu.curtin;

import edu.curtin.game.Game;
import edu.curtin.Localization;
import edu.curtin.gameplugins.PluginManager;
import edu.curtin.api.GameAPIImpl;
import edu.curtin.api.GameAPI;
import edu.curtin.scripts.ScriptManager;

import java.util.Locale;
import java.util.Scanner;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static Game game;
    private static Localization localization;
    private static GameAPI gameAPI;
    private static boolean pluginModeActive = false;
    private static int activePluginIndex = -1;
    private static ScriptManager scriptManager;
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(localization.getMessage("usage.message"));
            System.exit(1);
        }

        localization = new Localization();
        String inputFilePath = args[0];

        if (!inputFilePath.endsWith(".utf8.map") && !inputFilePath.endsWith(".utf16.map") && !inputFilePath.endsWith(".utf32.map")) {
           System.err.println(localization.getMessage("error.reading.input").replace("{0}", "Invalid file extension. Use .utf8.map, .utf16.map, or .utf32.map."));
           System.exit(1);
        }

        game = new Game(inputFilePath, Locale.getDefault());
        try {
            game.setup();
        } catch (IOException | ParseException e) {
            logger.log(Level.SEVERE, "Error setting up the game", e);
            System.err.println(localization.getMessage("error.reading.input").replace("{0}", e.getMessage()));
    System.exit(1);
        }

        gameAPI = new GameAPIImpl(game, game.getPlayer(), game.getGrid());


    

        try (Scanner scanner = new Scanner(System.in)) { 
            System.out.println(localization.getMessage("welcome.message"));

            while (true) {
                System.out.println();
                System.out.println("************************************************");
                System.out.println();
                System.out.println(game.displayGrid());
                System.out.println(game.displayInventory());
                System.out.println();
                System.out.println("************************************************");

                // Execute the active plugin if plugin mode is active
                if (pluginModeActive) {
                    PluginManager.getInstance().executePlugin(activePluginIndex, gameAPI);
                    System.out.println(localization.getMessage("plugin.mode.active")); // plugin mode is active
                } else {
                    System.out.println(localization.getMessage("plugin.mode.inactive")); // plugin mode is inactive
                }

                System.out.print(localization.getMessage("enter.move"));
                System.out.println();

                try {
                    String input = scanner.nextLine().trim().toLowerCase();

                    if (input.equals("q")) {
                        if (pluginModeActive) {
                            // Exit plugin mode
                            pluginModeActive = false;
                            System.out.println(localization.getMessage("exiting.plugin.mode"));
                        } else {
                            // Exit game
                            System.out.println(localization.getMessage("quitting.game"));
                            System.out.println(localization.getMessage("total.days.passed").replace("{0}", String.valueOf(game.getMoves())));
                            break;
                        }
                    }

                    if (input.equals("l")) {
                        showLocaleMenu(scanner);
                        continue;
                    }

                    if (input.equals("k")) {
                      if (scriptManager == null) {
                          synchronized (Main.class) { // Synchronization block
                            if (scriptManager == null) { // Double-checked locking
                                 scriptManager = ScriptManager.getInstance();
                                 scriptManager.setGameAPI(gameAPI);
                            }
                          }
                      }
                     showScriptMenu(scanner);
                     continue;
                    }

                    if (input.equals("p")) {
                        if (PluginManager.getInstance() == null) {
                            PluginManager.getInstance().setGameAPI(gameAPI);
                        }
                        showPluginMenu(scanner);
                        continue;
                    }

                    if (input.equals("w") || input.equals("a") || input.equals("s") || input.equals("d")) {
                        game.movePlayer(input);

                        if (game.isGameOver()) {
                            System.out.println(localization.getMessage("goal.reached").replace("{0}", String.valueOf(game.getMoves())));
                            System.out.println(localization.getMessage("total.days.passed").replace("{0}", String.valueOf(game.getMoves())));
                            break;
                        }
                    } else {
                        System.out.println(localization.getMessage("invalid.input"));
                    }
                
                } catch (Exception e) {
                    
                    System.out.println(localization.getMessage("error.reading.input").replace("{0}", e.getMessage()));
                    break;
                }

                if (pluginModeActive) {
                    System.out.println(localization.getMessage("plugin.mode.active"));
                } else {
                    System.out.println(localization.getMessage("plugin.mode.inactive"));
                }
            }
        } // Scanner will be closed automatically here
    
    }

    // script main menu
    private static void showScriptMenu(Scanner scanner) {
        System.out.println(localization.getMessage("script.menu.prompt"));
        System.out.println("1. Run hardcoded script"); // this option works
        System.out.println("2. Run script from input file"); // run this with only testinput-0.utf8

        
        int choice = Integer.parseInt(scanner.nextLine().trim());

        switch (choice) {
            case 1:
                runHardcodedScript();
                break;
            case 2:
                runScriptFromInput();
                break;
            default:
                System.out.println(localization.getMessage("script.invalid.input"));
        }
    }

    // Hardcoded script for teleportation
    private static void runHardcodedScript() {   // this part was done to check if the script logic works if i hardcode. the runscriptfrominput method is the script logic reading from a inputfile(which does read the script from the inputfile successfully but does not work)
        String script = "# Python script embedded for teleportation\n"
                      + "from java.util import Random\n"
                      + "def teleportPlayer(api):\n"
                      + "    random = Random()\n"
                      + "    rows = api.getGridRows()\n"
                      + "    columns = api.getGridColumns()\n"
                      + "    random_row = random.nextInt(rows)\n"
                      + "    random_column = random.nextInt(columns)\n"
                      + "    api.setPlayerPosition(random_row, random_column)\n"
                      + "    api.displayGrid()\n"
                      + "    print('Teleported to ({}, {})'.format(random_row, random_column))\n"
                      + "teleportPlayer(api)\n";
        scriptManager.executeScript(gameAPI, script);  // Execute the embedded script
    }

    // Run script from input file
    private static void runScriptFromInput() {
        String script = scriptManager.getLastAddedScript();
        if (script != null) {
            System.out.println();
            System.out.println(localization.getMessage("running.script"));
            scriptManager.executeScript(gameAPI, script);  
        } else {
            System.out.println(localization.getMessage("no.script"));
        }
    }

    private static void showPluginMenu(Scanner scanner) {
     System.out.println(localization.getMessage("choose.plugin"));
     System.out.println(localization.getMessage("available.plugins"));

     int i = 1;
     for (String pluginName : PluginManager.getInstance().getPluginNames()) {
        System.out.println(i + ". " + pluginName);
        i++;
     }

     System.out.println((i) + ". " + localization.getMessage("go.back.to.core")); // to go back to core gameplay

     System.out.print(localization.getMessage("enter.plugin.choice"));
     int choice = Integer.parseInt(scanner.nextLine().trim());

     if (choice == i) { // Go back to core gameplay
        pluginModeActive = false;
        activePluginIndex = -1; // Reset active plugin index
        System.out.println(localization.getMessage("back.to.core"));
     } else if (choice > 0 && choice < i) {
        activePluginIndex = choice; // Set active plugin index
        pluginModeActive = true; // Ensure plugin mode is active
        PluginManager.getInstance().executePlugin(choice, gameAPI);
     } else {
        System.out.println(localization.getMessage("pluginmenu.invalid.input"));
     }
    }

    private static void showLocaleMenu(Scanner scanner) {
     System.out.println(localization.getMessage("locale.menu.prompt"));
     System.out.println("1. Select from available languages");
     System.out.println("2. Enter a IETF language tag");

     System.out.print(localization.getMessage("locale.menu.choice"));
     int choice = Integer.parseInt(scanner.nextLine().trim());

     switch (choice) {
        case 1:
            showAvailableLanguages(scanner);
            break;
        case 2:
            enterLanguageTag(scanner);
            break;
        default:
            System.out.println(localization.getMessage("locale.invalid.input"));
            break;
     }
    }

    private static void showAvailableLanguages(Scanner scanner) {
     System.out.println("Available Languages:");
     System.out.println("1. Latin (la)");
     System.out.println("2. English (en-US)");
     System.out.println("3. French (fr-FR)");
     System.out.println("4. Spanish (es-ES)");
     System.out.println("5. German (de-DE)");

     System.out.print(localization.getMessage("locale.menu.choice"));
     int choice = Integer.parseInt(scanner.nextLine().trim());

     Locale newLocale;
     switch (choice) {
        case 1:
            newLocale = Locale.forLanguageTag("la");
            break;
        case 2:
            newLocale = Locale.forLanguageTag("en-US");
            break;
        case 3:
            newLocale = Locale.forLanguageTag("fr-FR");
            break;
        case 4:
            newLocale = Locale.forLanguageTag("es-ES");
            break;
        case 5:
            newLocale = Locale.forLanguageTag("de-DE");
            break;
        default:
            System.out.println(localization.getMessage("locale.invalid.input"));
            return;
     }

     game.updateLocale(newLocale); 
     localization.setLocale(newLocale.toLanguageTag());
     System.out.println();
     System.out.println(localization.getMessage("locale.changed").replace("{0}", newLocale.getDisplayName()));
    }

    private static void enterLanguageTag(Scanner scanner) {
     System.out.println("Enter an IETF language tag (e.g., la, en-US, fr-FR, es-ES, de-DE):");
     String languageTag = scanner.nextLine().trim();

     
     localization.setLocale(languageTag);
     System.out.println();
     System.out.println(localization.getMessage("locale.changed").replace("{0}", languageTag));
    }



}

