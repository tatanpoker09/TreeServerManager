package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.util.List;

public class CommandManager {
	private List<Command> commands;
	
	public CommandManager() {
		
	}
	
	public void setup() {
		
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
