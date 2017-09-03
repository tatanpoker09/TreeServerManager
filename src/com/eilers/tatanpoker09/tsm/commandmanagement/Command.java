package com.eilers.tatanpoker09.tsm.commandmanagement;

/**
 * Represents a command, either a main command or a subcommand.
 * A command is a piece of code (function), that gets triggered with specific configurations after it is called by its name
 * In this case by a string. So it goes String -> parse -> identify command -> call subcommand if found -> call default command if not found.
 * @author tatanpoker09
 *
 */
public interface Command {
	/**
	 * Command containing the command's actions.
	 * @param args - the extra configurations the command should have.
	 */
	boolean onTrigger(String topic, String[] args);

    /**
     * The default call if no subcommand is found in the base command.
     *
     * @param topic - The command topic.
     * @param args  - The payload separated into different arguments (Separated by split(","))
     */
    void defaultTrigger(String topic, String[] args);

    /**
     * Checks if the parameter topic belongs to this command.
     * @param topic - Topic to be checked.
     * @return if the topic belongs.
     */
    boolean isTopic(String topic);

    /**
     * Gets this command's topic
     * @return the topic
     */
    String getTopic();

    /**
     * Gets the callback from the command. If there is no callback, it must return null.
     *
     * @param args - Any parameters that should be used to build the callback.
     * @return The callback as a String, to be sent as a payload to topic/callback
     */
    String getCallback(String... args);
}