package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.commands.LightsCommand;

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
}
