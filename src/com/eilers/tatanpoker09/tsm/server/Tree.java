package com.eilers.tatanpoker09.tsm.server;

import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;

import java.util.logging.Logger;


/*
 * Current TODO:
 * .- Finish Command System
 * .- Finish Server Manager
 * .- Start Voice Manager
 * .- Start Bluetooth Manager
 * .- Start Peripheral Manager
 * .- Load up all managers.
 * .- Boot from Raspberry PI.
 * .- Start and Finish LightsCommand
 * .- Get a First Version done with Lights Working. For this no full Client side will be needed, just a way to simulate it.
  */
/**
 * Main class. In here the main program gets setup and Server Manager gets loaded up.
 * @author tatanpoker09
 *
 */
public class Tree {
	private static final String PROGRAM_NAME = "Tree Server Manager";
	private static final int MAXIMUM_CONNECTIONS = 5;
	
	private static ServerManager server;
	
	private static Logger log;
	
	/**
	 * Start of the program, loads up the logger, starts the server with a name and max connections and loads up the server setup.
	 * @param args - The arguments to start the program.
	 */
	public static void main(String[] args) {
		loadLogger();
		ServerManager serverManager = new ServerManager(PROGRAM_NAME, MAXIMUM_CONNECTIONS);
		Tree.server = serverManager;
		getLog().info("Server Started.");
        if (!serverManager.setup()) {
            log.severe("An error ocurred while loading up the server. Check your log.");
            System.exit(0);
        }
    }

	/**
	 * Starts up the logging and sets it to a global variable.
	 */
	private static void loadLogger() {
		Logger log = Logger.getLogger(PROGRAM_NAME);
		log.info("Logger Loaded.");
		Tree.log = log;
	}

	public static Logger getLog() {
		return log;
	}
	
	public static ServerManager getServer() {
		return server;
	}

    public static CommandManager getCommandManager() {
        return server.getcManager();
    }
}
