package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.net.InetAddress;

/**
 * Represents a command, either a main command or a subcommand.
 * A command is a piece of code (function), that gets triggered with specific configurations after it is called by its name
 * In this case by a string. So it goes String -> parse -> identify command -> call command.
 * @author tatanpoker09
 *
 */
public interface Command {
	/**
	 * Command containing the command's actions.
	 * @param args - the extra configurations the command should have.
	 */
	public abstract void onTrigger(String[] args);
	
	/**
	 * Function used to call the command from a specific address. 
	 * Used to log extra connections.
	 * @param args - the extra configurations the command should have.
	 * @param ip - The address.
	 */
	public void call(String[] args, InetAddress ip);
}
