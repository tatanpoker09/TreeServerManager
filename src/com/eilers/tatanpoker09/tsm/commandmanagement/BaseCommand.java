package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.net.InetAddress; 
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.server.Tree;
import com.eilers.tatanpoker09.tsm.commandmanagement.Command;

import javax.print.DocFlavor;

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
	private String topic;
	
	/**
	 * Constructor, creates a Command with a specified name.
	 * @param topic - The command topic.
	 */
	public BaseCommand(String topic) {
		this.topic = topic;
		this.setSubCommands(new ArrayList<SubCommand>());
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
		log.info("Adding to: "+this.getTopic()+" the subcommand: "+sCommand.getName());
		subCommands.add(sCommand);
	}
	
	/**
	 * Function that gets triggered after a client connection calls the command.
	 */
	public boolean onTrigger(String topic, String[] args){
        String subcommand = topic.replace(this.topic+"/", "");
        for(SubCommand subCommand : getSubCommands()) {
            if(subCommand.getName().equals(subcommand)){
                subCommand.onTrigger(topic, args);
                return true;
            }
        }
        return false;
    }

    public boolean isTopic(String topic) {
	    return topic.contains(this.topic.toLowerCase());
    }

        /**
         * If no subcommand is found, this will run.
         */
    //It is in fact better than a normal default case as you can work with the topic this way.
    @Override
    public abstract void defaultTrigger(String topic, String[] args);

	public List<SubCommand> getSubCommands() {
		return subCommands;
	}

	public void setSubCommands(List<SubCommand> subCommands) {
		this.subCommands = subCommands;
	}
	
	public String getTopic() {
		return topic;
	}

}
