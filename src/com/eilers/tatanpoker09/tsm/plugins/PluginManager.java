package com.eilers.tatanpoker09.tsm.plugins;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Created by tatanpoker09 on 23-08-2017.
 */
public class PluginManager implements Manager, Callable {
    private static final String PLUGIN_DIRECTORY = "plugins/";
    private List<TreePlugin> plugins;


    public PluginManager() {
        setup();
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
        startPlugins();
        return true;
    }

    private void startPlugins() {
        for (TreePlugin plugin : plugins) {
            plugin.load();
        }
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

                    try {
                        YamlReader reader = new YamlReader(new FileReader(getYMLFile(pluginsDirectories)));
                        Map map = (Map) reader.read();
                        String name = (String) map.get("name");
                        String author = (String) map.get("author");
                        String mainClass = (String) map.get("main");

                        ClassLoader classLoader = new URLClassLoader(new URL[]{getJarFile(pluginsDirectories)});
                        Class cl = classLoader.loadClass(mainClass);
                        TreePlugin tp = (TreePlugin) cl.newInstance();
                        tp.setName(name);
                        tp.setAuthor(author);
                        registerPlugin(tp);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (YamlException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                    found++;
                }
            }
        }
        return found;
    }

    private void registerPlugin(TreePlugin tp) {
        Logger log = Tree.getLog();
        plugins.add(tp);
        log.info("Added plugin: " + tp.getName() + " from " + tp.getAuthor());

    }

    private File getYMLFile(File pluginsDirectories) {
        File[] pluginFiles = pluginsDirectories.listFiles();
        boolean hasYMLData = false, JARFile = false;
        for (File pluginFile : pluginFiles) {
            if (pluginFile.getName().contains(".yml")) return pluginFile;
        }
        return null;
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

    public List<TreePlugin> getPlugins() {
        return plugins;
    }

    public void unloadPlugin(TreePlugin plugin) {
        plugin.unload();
    }

    public URL getJarFile(File pluginDirectories) {
        File[] pluginFiles = pluginDirectories.listFiles();
        boolean hasYMLData = false, JARFile = false;
        for (File pluginFile : pluginFiles) {
            if (pluginFile.getName().contains(".jar")) try {
                return pluginFile.toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
