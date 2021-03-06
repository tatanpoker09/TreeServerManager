package com.eilers.tatanpoker09.tsm.server;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import com.eilers.tatanpoker09.tsm.database.DatabaseManager;
import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;
import com.eilers.tatanpoker09.tsm.peripherals.PeripheralManager;
import com.eilers.tatanpoker09.tsm.plugins.PluginManager;
import com.eilers.tatanpoker09.tsm.plugins.TreePlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Handles server connections and setup. Pretty much this is the server itself.
 */
public class ServerManager implements Manager {
    private final ExecutorService threadpool = Executors.newFixedThreadPool(5); //One per manager.

	private String serverName;
	private int maximumConnections;
	private boolean running;
	
	private CommandManager cManager;
	private PeripheralManager pManager;
    private DatabaseManager dManager;
    private PluginManager pluginManager;
    private MQTTManager mqttManager;

	public ServerManager(String serverName, int maximumConnections) {
		this.serverName = serverName;
		this.maximumConnections = maximumConnections;
		this.running = true;
	}
	
	/**
	 * Loads all managers
	 */
    public boolean setup() {
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownManager()));

        Logger log = Tree.getLog();
		List<Future> setupTasks = new ArrayList<Future>();
		log.info("Setting up server: "+serverName);

		CommandManager cManager = new CommandManager();
		Future future = threadpool.submit(cManager);
		setupTasks.add(future);
		this.cManager = cManager;


		PeripheralManager pManager = new PeripheralManager();
		future = threadpool.submit(pManager);
		setupTasks.add(future);
		this.pManager = pManager;
		pManager.setup();

        DatabaseManager dManager = new DatabaseManager();
        future = threadpool.submit(dManager);
        setupTasks.add(future);
        this.dManager = dManager;

        if (!dManager.setup()) {
            //We couldn't connect to the database
            log.severe("Couldn't connect to the MySQL server! Check to start it up.");
            return false;
        }

		boolean done;
		do{
			done = true;
			for(Future f : setupTasks){
				if(!f.isDone()){
                    done = false;
                    break;
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(!done);

		log.info("Setup has been completed.");
		threadpool.shutdown();
		postSetup();
        return true;
    }
	
	/**
	 * Any post loading configurations are handled here.
	 */
    public void postSetup() {
        Logger log = Tree.getLog();
		MQTTManager mqttManager = new MQTTManager();
		mqttManager.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!mqttManager.isConnected()) {
			log.info("Check your MQTT Broker!");
            System.exit(0);
            return;
        }

        PluginManager pluginManager = new PluginManager();
        this.pluginManager = pluginManager;


		cManager.postSetup();
		dManager.postSetup();
		log.info("Post setup has been completed");
    }
	
	/**
	 * Opens the connection to the server, allowing Clients to Join.
	 * @param port
	 * @throws IOException
	 */
	public void openConnection(int port) throws IOException {
		Logger log = Tree.getLog();

		/* TODO CHECK INFO FROM MQTT CONNECTIONS */
		//log.info("Closing server!");
	}

	public int getMaximumConnections() {
		return maximumConnections;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public CommandManager getcManager() {
		return cManager;
	}

	public void setcManager(CommandManager cManager) {
		this.cManager = cManager;
	}

    public PeripheralManager getpManager() {
        return pManager;
    }

    public DatabaseManager getdManager() {
        return dManager;
    }

	public PluginManager getPluginManager() {
		return pluginManager;
	}
}

class ShutdownManager implements Runnable {

    @Override
    public void run() {
        Logger log = Tree.getLog();
        log.info("Running shutdown manager...");
        log.info("Closing bluetooth streams.");
        for (Peripheral p : Tree.getServer().getpManager().getPeripherals()) {
            p.closeStream();
        }
		log.info("Shutting down individual plugins...");
		for (TreePlugin plugin : Tree.getServer().getPluginManager().getPlugins()) {
			Tree.getServer().getPluginManager().unloadPlugin(plugin);
		}

    }
}