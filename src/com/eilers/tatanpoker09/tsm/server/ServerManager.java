package com.eilers.tatanpoker09.tsm.server;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import com.eilers.tatanpoker09.tsm.peripherals.BluetoothManager;
import com.eilers.tatanpoker09.tsm.peripherals.PeripheralManager;
import com.eilers.tatanpoker09.tsm.voice.VoiceManager;
import net.sf.xenqtt.client.*;
import net.sf.xenqtt.message.ConnectReturnCode;
import net.sf.xenqtt.message.QoS;

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
		int port = 7727;
/*
		Peripheral lights = new Peripheral("LIGHTS");
		lights.registerBtDevice(bManager.getFoundDevices().get(0));
        pManager.addPeripheral(lights);
		LightSection ls = new LightSection("",lights);
		ls.turn(true);*/


		TreeServerMQTTListener listener = new TreeServerMQTTListener();
		Logger log = Tree.getLog();

		MQTTManager mqttManager = new MQTTManager();
		mqttManager.start();

        try {
            openConnection(port);
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

    public BluetoothManager getbManager() {
        return bManager;
    }
}

class TreeServerMQTTListener implements MqttClientListener {

	final List<String> catalog = Collections.synchronizedList(new ArrayList<String>());

	public void publishReceived(MqttClient mqttClient, PublishMessage message) {
		System.out.println("Message recieved: "+message);
	}

	public void disconnected(MqttClient mqttClient, Throwable cause, boolean reconnecting) {
		Logger log = Tree.getLog();
		if (cause != null) {
			log.severe("Disconnected from the broker due to an exception: "+cause);
		} else {
			log.info("Disconnecting from the broker.");
		}

		if (reconnecting) {
			log.info("Attempting to reconnect to the broker.");
		}
	}
}
