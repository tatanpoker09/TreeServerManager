package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.net.InetAddress;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.server.Tree;

/**
 * Represents a subcommand. As the name states, it is a section of a BaseCommand.
 * Whenever you call a command, it identifies the BaseCommand, and then tries to identify a subcommand, if it fails it'll call the BaseCommand.
 * If it finds the specified subcommand, it'll run this class' call command.
 * @author tatanpoker09
 */
public class SubCommand implements Command{
	private String name;
	private String[] args;
	
	public SubCommand(String name) {
		this.name = name;
	}

	public void onTrigger(String[] args) {
		
	}

	public String getName() {
		return name;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	/**
	 * Method used to call the subcommand with a specified configuration.
	 */
	public void call(String[] args, InetAddress ip) {
		Logger log = Tree.getLog();
		log.info("Subcommand: "+name+" called by: "+ip.toString());
		onTrigger(args);
	}
}