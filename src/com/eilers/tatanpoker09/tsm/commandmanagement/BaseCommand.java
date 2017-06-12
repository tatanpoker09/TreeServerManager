package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.net.InetAddress; 
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.server.Tree;
import com.eilers.tatanpoker09.tsm.commandmanagement.Command;

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
	/**
	 * A list containing all loaded subCommands. Specified during command setup.
	 */
	private List<SubCommand> subCommands;
	private String name;
	
	/**
	 * Constructor, creates a Command with a specified name.
	 * @param name - The command name.
	 */
	public BaseCommand(String name) {
		this.name = name;
		setup();
	}
	
	/**
	 * Command used to setup subcommands.
	 */
	public abstract void setup();
	
	/**
	 * Adds a subcommand to the collection.
	 * @param sCommand - The Subcommand.
	 */
	public void addSubCommand(SubCommand sCommand) {
		Logger log = Tree.getLog();
		if(subCommands==null) {
			log.warning("Subcommand collection didn't exist, creating one now.");
			subCommands = new ArrayList<SubCommand>();
		}
		log.info("Adding to: "+this.getName()+" the subcommand: "+sCommand.getName());
		subCommands.add(sCommand);
	}
	
	/**
	 * Function that gets triggered after a client connection calls the command.
	 */
	public abstract void onTrigger(String[] args);
	
	/**
	 * Function to call when the command gets triggered from somewhere else, mainly a step for logging.
	 */
	public void call(String[] args, InetAddress ip) {
		
	}




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
