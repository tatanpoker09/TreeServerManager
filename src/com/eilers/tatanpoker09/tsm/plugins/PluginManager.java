package com.eilers.tatanpoker09.tsm.plugins;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.server.Tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Created by tatanpoker09 on 23-08-2017.
 */
public class PluginManager implements Manager, Callable {
    private static final String PLUGIN_DIRECTORY = "plugins/";
    private List<TreePlugin> plugins;


    public PluginManager() {


    }

    /**
     * @return If it just created the folder
     */
    @Override
    public boolean setup() {
        Logger log = Tree.getLog();
        File pluginFolder = new File(PLUGIN_DIRECTORY);
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
            log.info("Didn't find the plugins folder! Creating it now...");
            return false;
        }
        loadPlugins();
        return true;
    }

    /**
     * Loads plugins
     *
     * @return The amount of plugins loaded.
     */
    public int loadPlugins() {
        Logger log = Tree.getLog();
        File pluginFolder = new File(PLUGIN_DIRECTORY);
        int found = 0;
        plugins = new ArrayList<TreePlugin>();

        log.info("Loading plugins...");
        for (File pluginsDirectories : pluginFolder.listFiles()) {
            if (pluginsDirectories.isDirectory()) {
                //Verification of the directory.
                if (isValidPlugin(pluginsDirectories)) {
                    String folderName = pluginsDirectories.getName();
                    log.info("Parsing plugin folder found: " + folderName);
                    //TODO finish loading up the plugins.
                    found++;
                }
            }
        }
        return found;
    }

    private boolean isValidPlugin(File pluginsDirectories) {
        File[] pluginFiles = pluginsDirectories.listFiles();
        boolean hasYMLData = false, JARFile = false;
        for (File pluginFile : pluginFiles) {
            if (pluginFile.getName().contains(".yml")) hasYMLData = true;
            if (pluginFile.getName().contains(".jar")) JARFile = true;
        }
        return hasYMLData && JARFile;
    }

    @Override
    public void postSetup() {

    }

    @Override
    public Object call() throws Exception {
        return setup();
    }
}
