package edu.curtin.scripts;

import edu.curtin.api.GameAPI;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.List;

public class ScriptManager {
    private static volatile ScriptManager instance;
    private GameAPI gameAPI;
    private List<String> scripts;

    
    private ScriptManager() {
        scripts = new ArrayList<>(); // script storage
    }

    // Get the singleton instance of the ScriptManager
    public static ScriptManager getInstance() {
        if (instance == null) { 
            synchronized (ScriptManager.class) { // Lock to prevent multiple threads from entering
                if (instance == null) { 
                    instance = new ScriptManager();
                }
            }
        }
        return instance;
    }

    // Set the GameAPI instance to interact with the game
    public void setGameAPI(GameAPI api) {
        this.gameAPI = api;
        System.out.println("GameAPI instance set successfully.");
    }

    // Add a new script to the script list
    public void addScript(String script) {
        if (script != null && !script.isEmpty()) {
            scripts.add(script); // Add the script to the list
        } else {
            System.out.println("Cannot add an empty script.");
        }
    }
    
    

    public void executeScript(GameAPI gameAPI, String script) {
     if (gameAPI == null) {
        System.out.println("GameAPI instance not set.");
        return;
     }
     if (script == null || script.isEmpty()) {
        System.out.println("No script available to run.");
        return;
     }

     // Initialize the Python interpreter
     try (PythonInterpreter interpreter = new PythonInterpreter()) {
        interpreter.set("api", gameAPI);
        // Execute the script
        interpreter.exec(script);
        System.out.println("Script execution completed successfully.");
     } catch (org.python.core.PySyntaxError e) { // Catch specific syntax errors
        System.out.println("Syntax error in script: " + e.getMessage());
     } catch (org.python.core.PyException e) { // Catch other Python exceptions
        System.out.println("Error during script execution: " + e.getMessage());
     } catch (IllegalArgumentException e) { // Catch specific illegal argument exceptions
        System.out.println("Invalid argument provided: " + e.getMessage());
     }
    }



    // Retrieve a list of all scripts
    public List<String> getAllScripts() {
        return new ArrayList<>(scripts); // Return a copy of the script list
    }

    // Execute all scripts in the list
    public void executeAllScripts() {
        for (String script : scripts) {
            executeScript(gameAPI, script);
        }
    }
    
    // Get the last script added to the script list
    public String getLastAddedScript()
    {
        if (!scripts.isEmpty())
        {
            return scripts.get(scripts.size() - 1); // Return the most recently added script
        }
        return null; // Return null if no scripts are available
    }
}

