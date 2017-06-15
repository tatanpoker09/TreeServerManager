package com.eilers.tatanpoker09.tsm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.LightSection;
import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import com.eilers.tatanpoker09.tsm.peripherals.BluetoothManager;
import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;
import com.eilers.tatanpoker09.tsm.peripherals.PeripheralManager;
import com.eilers.tatanpoker09.tsm.voice.VoiceManager;

/**
 * Handles server connections and setup. Pretty much this is the server itself.
 */
public class ServerManager{
	private String serverName;
	private int maximumConnections;
	private boolean running;
	
	private CommandManager cManager;
	private BluetoothManager bManager;
	private PeripheralManager pManager;

	public ServerManager(String serverName, int maximumConnections) {
		this.serverName = serverName;
		this.maximumConnections = maximumConnections;
		this.running = true;
	}
	
	/**
	 * Loads all managers
	 */
	public void setup() {
		Logger log = Tree.getLog();
		log.info("Setting up server: "+serverName);
		CommandManager cManager = new CommandManager();
		cManager.setup();
		cManager.postSetup();
		
		VoiceManager vManager = new VoiceManager();
		vManager.recognize();
		
		BluetoothManager bManager = new BluetoothManager(this);
		bManager.setup();
		bManager.discoverDevices();

		pManager = new PeripheralManager();
		pManager.setup();
		postSetup();

	}
	
	/**
	 * Any post loading configurations are handled here.
	 */
	private void postSetup() {
		Peripheral lights = new Peripheral("LIGHTS");
		System.out.print("Found Devices:");
		System.out.println(bManager.getFoundDevices());
		lights.registerBtDevice(bManager.getFoundDevices().get(0));
		try {
			lights.getBtDevice().authenticate();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pManager.addPeripheral(lights);
		LightSection ls = new LightSection("",lights);
	}
	
	/**
	 * Opens the connection to the server, allowing Clients to Join.
	 * @param port
	 * @throws IOException
	 */
	public void openConnection(int port) throws IOException {
		Logger log = Tree.getLog();
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
}
