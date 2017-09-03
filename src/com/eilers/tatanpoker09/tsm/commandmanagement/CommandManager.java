package com.eilers.tatanpoker09.tsm.commandmanagement;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.commands.BluetoothCommand;
import com.eilers.tatanpoker09.tsm.server.MQTTManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import net.sf.xenqtt.client.Subscription;
import net.sf.xenqtt.message.QoS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Command Manager. Handles command loading, unloading, enabling.
 * And most importantly string to command parsing.
 * @author tatanpoker09
 *
 */
public class CommandManager implements Callable, Manager {
    /**
     * All commands loaded are stored in here.
     */
    private List<BaseCommand> commands;

    /**
     * Basic Constructor
     */
    public CommandManager() {
        setup();
    }

    /**
     * Loads all native commands.
     */
    private void loadCommands() {
        BluetoothCommand bluetooth = new BluetoothCommand();
        registerCommand(bluetooth);
    }


    /**
     * Sets everything up for command loading.
     */
	public boolean setup() {
		commands = new ArrayList<BaseCommand>();
		loadCommands();
        return true;
    }

    /**
     * Anything handled after the main setup needs to be loaded here.
     * In this case we get all commands loaded from the PluginManager and set up their MQTT subscriptions
     */
    public void postSetup() {
		List<Subscription> subscriptions = new ArrayList<>();
		for (BaseCommand c : Tree.getServer().getcManager().getCommands()) {
			String topic = c.getTopic();
			subscriptions.add(new Subscription(topic, QoS.AT_LEAST_ONCE));
			for (SubCommand sc : c.getSubCommands()) {
				subscriptions.add(new Subscription(topic + "/" + sc.getName(), QoS.AT_LEAST_ONCE));
			}
		}
		MQTTManager.getClient().subscribe(subscriptions);
	}

	public List<BaseCommand> getCommands() {
		return commands;
	}

	public void registerCommand(BaseCommand command) {
		this.commands.add(command);


	}

	public Boolean call() throws Exception {
		return setup();
	}

	/**
	 * @param topic - The topic the message was published in.
	 * @param payload - The actual message.
	 * @return if the command is a disconnect query.
	 */
    public boolean parseAndRun(String topic, String payload){
        System.out.println(topic+","+payload+" getting parsed");
        for(Command c : commands){
            if (c.isTopic(topic)) {
                String[] args = payload.split(",");
                if(!c.onTrigger(topic, args)){
                    c.defaultTrigger(topic, args);
                }
                break;
            }
        }
        return false;
    }

    /**
     * Publishes the callback to <topic>/callback after the command has been called.
     * @param c
     */
    public void publishCallback(Command c) {
        if (c.getCallback() != null) {
            MQTTManager.publish(c.getTopic() + "/callback", c.getCallback());
        }
    }

	private void run(){

	}
}
