package com.eilers.tatanpoker09.tsm.server;

import java.util.logging.Logger;

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
