package edu.curtin.gameplugins;

import edu.curtin.Localization;
import java.util.ArrayList;
import java.util.List;

public class PluginLoader {

    private PluginRegistry registry;
    private List<String> loadedPlugins;

    public PluginLoader(PluginRegistry registry) {
        this.registry = registry;
        this.loadedPlugins = new ArrayList<>(); // Initialize the loaded plugins list
    }

    // to load a plugin by its class name
    public void load(String pluginClassName, Localization localization) {
     if (pluginClassName == null || pluginClassName.isEmpty()) {  // Check if the provided class name is null or empty
        System.err.println(localization.getMessage("plugin.load.invalid.classname"));
        return;
     }

     try {
        Class<?> pluginClass = Class.forName(pluginClassName); // Attempt to load the class using the provided name
        if (!isPluginValid(pluginClass)) { // Validate if the class is a valid plugin
            System.err.println(localization.getMessage("plugin.load.invalid.type") + ": " + pluginClassName);
            return;
        }

        registry.registerPlugin(pluginClass); // Register the valid plugin in the registry
        loadedPlugins.add(pluginClassName); // Add the plugin class name to the loaded plugins list
        System.out.println(localization.getMessage("plugin.load.success") + ": " + pluginClassName);
     } catch (ClassNotFoundException e) {
        
        System.err.println(localization.getMessage("plugin.load.error") + ": " + e.getMessage()); // Handle when the class cannot be found
     } 

    }

    // to check if the plugin is valid
    private boolean isPluginValid(Class<?> pluginClass) {
        return hasDefaultConstructor(pluginClass) && implementsPluginInterface(pluginClass); // A plugin is valid if it has a default constructor and implements PluginInterface
    }

    private boolean hasDefaultConstructor(Class<?> pluginClass) {
        try {
            pluginClass.getDeclaredConstructor(); // Attempt to get the default constructor
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }


    // to check if the plugin class implements the PluginInterface
    private boolean implementsPluginInterface(Class<?> pluginClass) {
        for (Class<?> implementedInterface : pluginClass.getInterfaces()) {
            if (implementedInterface.getSimpleName().equals("PluginInterface")) {
                return true;
            }
        }
        return false;
    }

    // to get a list of loaded plugin class names
    public List<String> getLoadedPlugins() {
        return new ArrayList<>(loadedPlugins);
    }

    // to unload a specific plugin by its class name
    public void unload(String pluginClassName, Localization localization) {
        if (loadedPlugins.contains(pluginClassName)) {
            loadedPlugins.remove(pluginClassName);  // Remove it from the list
            System.out.println(localization.getMessage("plugin.unload.success") + ": " + pluginClassName);
        } else {
            System.err.println(localization.getMessage("plugin.unload.not.loaded") + ": " + pluginClassName);
        }
    }

    // to unload all currently loaded plugins
    public void unloadAll(Localization localization) {
        if (loadedPlugins.isEmpty()) {
            System.out.println(localization.getMessage("plugin.unload.none.loaded"));
            return;
        }

        for (String pluginClassName : new ArrayList<>(loadedPlugins)) {   
            unload(pluginClassName, localization);
        }
        System.out.println(localization.getMessage("plugin.unload.all.success"));
    }
}

