package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.util.List;

/**
 * Represents a command. Cannot be instantiated as it doesn't represent any specific command. Commands must extend this class.
 * Commands are specified for only server-client protocol communication.
 * The main syntaxis for a command is:
 * [command] [subcommand] [args]
 * 
 * For Voice Commands check VoiceManager.
 * @author tatanpoker09
 *
 */
public abstract class BaseCommand implements Command{
	private List<SubCommand> subCommands;
	private String name;
	
	public BaseCommand(String name) {
		this.name = name;
	}
	
	
	
	
	@Override
	public abstract void onTrigger(String[] args);




	public List<SubCommand> getSubCommands() {
		return subCommands;
	}

	public void setSubCommands(List<SubCommand> subCommands) {
		this.subCommands = subCommands;
	}
	
	public String getName() {
		return name;
	}

}
