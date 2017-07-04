package com.eilers.tatanpoker09.tsm.server;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.client.TreeClient;
import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import com.eilers.tatanpoker09.tsm.peripherals.BluetoothManager;
import com.eilers.tatanpoker09.tsm.peripherals.PeripheralManager;
import com.eilers.tatanpoker09.tsm.voice.VoiceManager;

/**
 * Handles server connections and setup. Pretty much this is the server itself.
 */
public class ServerManager{
	private final ExecutorService threadpool = Executors.newFixedThreadPool(5); //One per manager.

	private String serverName;
	private int maximumConnections;
	private boolean running;
	
	private CommandManager cManager;
	private BluetoothManager bManager;
	private VoiceManager vManager;
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
		List<Future> setupTasks = new ArrayList<Future>();
		log.info("Setting up server: "+serverName);

		CommandManager cManager = new CommandManager();
		Future future = threadpool.submit(cManager);
		setupTasks.add(future);
		this.cManager = cManager;
		
		VoiceManager vManager = new VoiceManager();
		vManager.recognize();
		future = threadpool.submit(vManager);
		setupTasks.add(future);
		this.vManager = vManager;

		BluetoothManager bManager = new BluetoothManager(this);
		future = threadpool.submit(bManager);
		setupTasks.add(future);
		this.bManager = bManager;

		PeripheralManager pManager = new PeripheralManager();
		future = threadpool.submit(pManager);
		setupTasks.add(future);
		this.pManager = pManager;
		pManager.setup();

		boolean done;
		do{
			done = true;
			for(Future f : setupTasks){
				if(!f.isDone()){
					done =false;
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
	}
	
	/**
	 * Any post loading configurations are handled here.
	 */
    protected void postSetup() {
		cManager.postSetup();
/*
		Peripheral lights = new Peripheral("LIGHTS");
		lights.registerBtDevice(bManager.getFoundDevices().get(0));
        pManager.addPeripheral(lights);
		LightSection ls = new LightSection("",lights);
		ls.turn(true);*/

        try {
            openConnection(7727);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Opens the connection to the server, allowing Clients to Join.
	 * @param port
	 * @throws IOException
	 */
	public void openConnection(int port) throws IOException {
		Logger log = Tree.getLog();
		ServerSocket serverSocket = new ServerSocket(port);
		log.info("Starting listening loop.|");
		while(running) {
			Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
                TreeClient tc = new TreeClient(clientSocket);
                tc.run();
				log.info("Client "+clientSocket.getInetAddress()+" has pre-connected to the server.");
			} catch (IOException e) {
				log.severe("Client had an error connecting to the server: "+e.getStackTrace());
				throw new IOException("Error connecting to the server.");
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
