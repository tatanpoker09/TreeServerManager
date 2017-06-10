package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.util.ArrayList;
import java.util.List;

/**
 * Command Manager. Handles command loading, unloading, enabling.
 * And most importantly string to command parsing.
 * @author tatanpoker09
 *
 */
public class CommandManager {
	private List<Command> commands;
	
	public CommandManager() {
		
	}
	/**
	 * Loads all commands and stores them in the "commands" List.
	 */
	public void setup() {
		commands = new ArrayList<Command>();
	}
	
	public void postSetup() {
		
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void addCommand(Command command) {
		this.commands.add(command);
	}
}
