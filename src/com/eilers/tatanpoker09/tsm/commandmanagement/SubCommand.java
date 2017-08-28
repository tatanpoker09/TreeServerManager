package com.eilers.tatanpoker09.tsm.commandmanagement;

/**
 * Represents a subcommand. As the name states, it is a section of a BaseCommand.
 * Whenever you call a command, it identifies the BaseCommand, and then tries to identify a subcommand, if it fails it'll call the BaseCommand.
 * If it finds the specified subcommand, it'll run this class' call command.
 * @author tatanpoker09
 */
public class SubCommand implements Command{
	private String name;
	private String[] args;
	private CommandTrigger ctrigger;
	
	public SubCommand(String name, CommandTrigger trigger) {
		this.name = name;
		this.ctrigger = trigger;
	}

    @Override
    public boolean onTrigger(String topic, String[] args) {
        ctrigger.call(topic, args);
        return true;
    }

    @Override
    public void defaultTrigger(String topic, String[] args) {

    }

    @Override
    public boolean isTopic(String topic) {
        return false;
    }

    @Override
    public String getTopic() {
		return null;
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
}