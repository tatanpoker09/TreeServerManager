package com.eilers.tatanpoker09.tsm.commandmanagement;

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
	boolean onTrigger(String topic, String[] args);
    void defaultTrigger(String topic, String[] args);
    boolean isTopic(String topic);

    String getTopic();
}