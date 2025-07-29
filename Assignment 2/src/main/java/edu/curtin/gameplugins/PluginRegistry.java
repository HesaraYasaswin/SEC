package edu.curtin.gameplugins;

import java.util.HashMap;
import java.util.Map;

public class PluginRegistry {

    private Map<String, Class<?>> pluginMap;

    public PluginRegistry() {
        pluginMap = new HashMap<>();
    }

    public void registerPlugin(Class<?> pluginClass) {  // to register a plugin by its Class object
        String pluginName = pluginClass.getSimpleName();
        pluginMap.put(pluginName, pluginClass);
    }

    public Class<?> getPluginClass(String pluginName) {  // to retrieve the Class object of a plugin by its name
        return pluginMap.get(pluginName);
    }

    public String[] getPluginNames() {  // Method to get an array of all registered plugin names
        return pluginMap.keySet().toArray(new String[0]);
    }

    public void removePlugin(String pluginName) {  // Method to remove a plugin from the registry by its name
        pluginMap.remove(pluginName);
    }

    public boolean isPluginRegistered(String pluginName) {  // Method to check if a plugin is registered in the registry
        return pluginMap.containsKey(pluginName);
    }
}

