package com.eilers.tatanpoker09.tsm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;

/**
 * Handles server connections and setup. Pretty much this is the server itself.
 */
public class ServerManager extends Thread{
	private String serverName;
	private int maximumConnections;
	private boolean running;
	
	
	private CommandManager cManager;
	
	public ServerManager(String serverName, int maximumConnections) {
		this.serverName = serverName;
		this.maximumConnections = maximumConnections;
		this.running = true;
	}
	
	public void setup() {
		CommandManager cManager = new CommandManager();
		cManager.setup();
		cManager.postSetup();
		postSetup();
	}
	
	private void postSetup() {
		
	}
	
	/**
	 * Opens the connection to the server, allowing Clients to Join.
	 * @param port
	 * @throws IOException
	 */
	public void openConnection(int port) throws IOException {
		Logger log = TreeServerManager.getLog();
		ServerSocket serverSocket = new ServerSocket(port);
		while(running) {
			Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
				log.info("Client "+clientSocket.getInetAddress()+" has pre-connected to the server.");
			} catch (IOException e) {
				log.severe("Client had an error connecting to the server: "+e.getStackTrace());
			}
		}
		serverSocket.close();
		log.info("Closing server!");
	}
}
