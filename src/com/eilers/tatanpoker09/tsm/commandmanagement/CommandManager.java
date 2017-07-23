package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.commands.LightsCommand;
import com.eilers.tatanpoker09.tsm.peripherals.BluetoothManager;
import com.eilers.tatanpoker09.tsm.server.Tree;

/**
 * Command Manager. Handles command loading, unloading, enabling.
 * And most importantly string to command parsing.
 * @author tatanpoker09
 *
 */
public class CommandManager implements Callable, Manager {
	private List<Command> commands;
	
	public CommandManager() {
		
	}
	
	
	/**
	 * Loads all commands and stores them in the "commands" List.
	 */
	public boolean setup() {
		commands = new ArrayList<Command>();
		LightsCommand lights = new LightsCommand();
		commands.add(lights);
		return true;
	}
	
	public void postSetup() {
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void addCommand(Command command) {
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
		if(topic.equals("manager/bluetooth/")){
		    if(payload.equals("search")){
                BluetoothManager bm = Tree.getServer().getbManager();
                bm.discoverDevices();
            }
        }

		return false;
	}

	private void run(){

	}
}
