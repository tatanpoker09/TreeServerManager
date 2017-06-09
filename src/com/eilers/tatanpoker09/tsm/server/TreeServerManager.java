package com.eilers.tatanpoker09.tsm.server;

import java.util.logging.Logger;


/*
 * Current TODO:
 * .- Finish Command System
 * .- Finish Server Manager
 * .- Start Voice Manager
 * .- Start Bluetooth Manager
 * .- Start and Finish LightsCommand
 * .- Get a First Version done with Lights Working. For this no full Client side will be needed, just a way to simulate it.
  */
/**
 * Main class. In here the main program gets setup and Server Manager gets loaded up.
 * @author tatanpoker09
 *
 */
public class TreeServerManager {
	private static final String PROGRAM_NAME = "Tree Server Manager";
	private static final int MAXIMUM_CONNECTIONS = 5;
	private static Logger log;
	
	public static void main(String[] args) {
		loadLogger();
		ServerManager serverManager = new ServerManager(PROGRAM_NAME, MAXIMUM_CONNECTIONS);
		getLog().info("Server Started.");
		serverManager.setup();
	}

	private static void loadLogger() {
		Logger log = Logger.getLogger(PROGRAM_NAME);
		log.info("Logger Loaded.");
		TreeServerManager.log = log;
	}

	public static Logger getLog() {
		return log;
	}
}
