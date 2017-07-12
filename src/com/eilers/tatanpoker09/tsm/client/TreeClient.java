package com.eilers.tatanpoker09.tsm.client;

import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import net.sf.xenqtt.client.*;
import net.sf.xenqtt.message.ConnectReturnCode;
import net.sf.xenqtt.message.QoS;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a Client Connection, handles the connection with a single client.
 * @author tatanpoker09
 *
 */
public class TreeClient extends Thread {
	private String user;
	private String ip;
	private final String port = "7727";
	
	public TreeClient(String ip) {
	    super();
	    this.ip = ip;
	}

	@Override
    public void run(){
		Logger log = Tree.getLog();
		TreeClientMQTTListener listener = new TreeClientMQTTListener();
		SyncMqttClient client = new SyncMqttClient("tcp://localhost:"+port, listener, 5);
		ConnectReturnCode returnCode = client.connect("localAdmin", true);
		if (returnCode != ConnectReturnCode.ACCEPTED) {
			log.severe("Unable to connect to the MQTT broker. Reason: " + returnCode);
			return;
		}

		// Create your subscriptions. In this case we want to build up a catalog of classic rock.
		List<Subscription> subscriptions = new ArrayList<Subscription>();
		subscriptions.add(new Subscription("main/tatanroom/lights/on", QoS.AT_LEAST_ONCE));

    }

    public void sendMessage(String message) throws IOException {
    }
}

class TreeClientMQTTListener implements MqttClientListener {

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
