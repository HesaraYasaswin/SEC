package edu.curtin.gameplugins;

import edu.curtin.api.GameAPI;
import edu.curtin.Localization;
import java.lang.reflect.InvocationTargetException;

public class PluginExecutor {

    private PluginRegistry registry;

    public PluginExecutor(PluginRegistry registry) {
        this.registry = registry;
    }

    // Method to execute a plugin based on its index
    public void execute(int pluginIndex, GameAPI api, Localization localization) {
        String pluginName = getPluginNameByIndex(pluginIndex);  // Get the name of the plugin using the provided index
        if (pluginName != null) {  // if a valid plugin name was retrieved
            Class<?> pluginClass = registry.getPluginClass(pluginName);  // Retrieve the plugin's class using the name
            if (pluginClass != null) {
                try {
                    Object pluginInstance = pluginClass.getDeclaredConstructor(GameAPI.class).newInstance(api); // Create a new instance of the plugin using reflection
                    invokePluginAction(pluginClass, pluginInstance);  // Invoke the action method of the plugin
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | 
                         InvocationTargetException e) {
                    // Handle specific exceptions related to reflection
                    System.err.println(localization.getMessage("plugin.execute.error") + ": " + e.getMessage());
                }
            }
        }
    }

    // method to get the plugin name by its index
    private String getPluginNameByIndex(int index) {
        String[] pluginNames = registry.getPluginNames();
        return (index > 0 && index <= pluginNames.length) ? pluginNames[index - 1] : null;
    }

    // method to invoke the action method of the plugin instance
    private void invokePluginAction(Class<?> pluginClass, Object pluginInstance) throws NoSuchMethodException, 
            IllegalAccessException, InvocationTargetException {
        // Invoke the plugin's action method
        pluginClass.getMethod("onPluginAction").invoke(pluginInstance);
    }
}

