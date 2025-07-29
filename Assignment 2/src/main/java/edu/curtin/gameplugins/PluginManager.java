package edu.curtin.gameplugins;

import edu.curtin.api.GameAPI;
import edu.curtin.Localization;
import java.util.Locale;

public class PluginManager {

    private static volatile PluginManager instance;
    private PluginRegistry registry;
    private PluginLoader loader;
    private PluginExecutor executor;
    private GameAPI gameAPI; 
    private Localization localization;

    private PluginManager() {
        registry = new PluginRegistry();
        loader = new PluginLoader(registry);
        executor = new PluginExecutor(registry);
        localization = new Localization();
    }

    // Double-checked locking for thread-safe singleton
    public static PluginManager getInstance() {
        if (instance == null) {
            synchronized (PluginManager.class) {
                if (instance == null) {
                    instance = new PluginManager();
                }
            }
        }
        return instance;
    }

    public void loadPlugin(String pluginClassName) {
        loader.load(pluginClassName, localization);
    }

    public void executePlugin(int pluginIndex, GameAPI api) {
        executor.execute(pluginIndex, api, localization);
    }

    public void setGameAPI(GameAPI api) {
        this.gameAPI = api; 
    }

    public GameAPI getGameAPI() {
        return gameAPI; 
    }

    public String[] getPluginNames() {
        return registry.getPluginNames(); // Use the registry to get plugin names
    }

    public void registerPlugin(Class<?> pluginClass) {
        registry.registerPlugin(pluginClass); // Delegate registration to PluginRegistry
    }

    public void setLocale(Locale locale) {
        localization.setLocale(locale.toLanguageTag());
    }
}

