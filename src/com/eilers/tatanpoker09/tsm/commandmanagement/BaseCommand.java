package com.eilers.tatanpoker09.tsm.commandmanagement;

import com.eilers.tatanpoker09.tsm.server.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a command. Cannot be instantiated as it doesn't represent any specific command. Commands must extend this class.
 * Commands are specified for only server-client protocol communication.
 * The main syntaxis for a command is:
 * [Command Topic]/[Subcommand], args
 * In the form of topic, payload.
 * For more information Check MQTT protocol communication.
 *
 * @author tatanpoker09
 *
 */
public abstract class BaseCommand implements Command{
	/**
	 * A list containing all loaded subCommands. Specified during command setup.
	 */
	private List<SubCommand> subCommands;
    /**
     * The command topic to register to MQTT.
     */
    private String topic;
    /**
     * What is sent back to the client after and if the command default call has been called.
     */
    private CommandTrigger callback;

    /**
     * Constructor, creates a Command with a specified name.
     * Use if your command default call doesn't need a callback.
     * @param topic - The command topic.
	 */
    public BaseCommand(String topic) {
        this.topic = topic;
        this.setSubCommands(new ArrayList<SubCommand>());
		setup();
	}

    /**
     * Constructor, creates a Command with a specified name.
     *
     * @param topic    - The command topic.
     * @param callback - The callback builder.
     */
    public BaseCommand(String topic, CommandTrigger callback) {
        this.topic = topic;
        this.setSubCommands(new ArrayList<SubCommand>());
        this.callback = callback;
        setup();
    }

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
		log.info("Adding to: "+this.getTopic()+" the subcommand: "+sCommand.getName());
		subCommands.add(sCommand);
	}
	
	/**
	 * Function that gets triggered after a client connection calls the command.
	 */
    public boolean onTrigger(String topic, String[] args){
        String subcommandname = topic.replace(this.topic + "/", "");
        for(SubCommand subCommand : getSubCommands()) {
            if (subCommand.getName().equals(subcommandname)) {
                subCommand.onTrigger(topic, args);
                Tree.getServer().getcManager().publishCallback(subCommand);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inputted topic is part of this command.
     * @param topic - The topic to be checked.
     * @return - If the topic is from this command.
     */
    public boolean isTopic(String topic) {
        return topic.contains(this.topic.toLowerCase());
    }

        /**
         * If no subcommand is found, this will run.
         * Default command call.
         */
    //It is in fact better than a normal default case as you can work with the topic this way.
    @Override
    public abstract void defaultTrigger(String topic, String[] args);

    /**
     * Command used to setup subcommands.
     */
    public abstract void setup();

	public List<SubCommand> getSubCommands() {
		return subCommands;
	}

	public void setSubCommands(List<SubCommand> subCommands) {
		this.subCommands = subCommands;
	}
	
	public String getTopic() {
		return topic;
    }

    public String getCallback(String... args) {
        return callback.buildCallback(args);
    }

}
