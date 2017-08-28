package com.eilers.tatanpoker09.tsm.commandmanagement;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.commands.BluetoothCommand;
import com.eilers.tatanpoker09.tsm.commands.LightsCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Command Manager. Handles command loading, unloading, enabling.
 * And most importantly string to command parsing.
 * @author tatanpoker09
 *
 */
public class CommandManager implements Callable, Manager {
    private List<BaseCommand> commands;

    public CommandManager() {
	    setup();
	}

    private void loadCommands() {
        LightsCommand lights = new LightsCommand();
        commands.add(lights);
        BluetoothCommand bluetooth = new BluetoothCommand();
        commands.add(bluetooth);
    }


    /**
	 * Loads all commands and stores them in the "commands" List.
	 */
	public boolean setup() {
        commands = new ArrayList<BaseCommand>();
        loadCommands();
        return true;
	}
	
	public void postSetup() {
	}

    public List<BaseCommand> getCommands() {
        return commands;
	}

    public void addCommand(BaseCommand command) {
        this.commands.add(command);
	}

	public Boolean call() throws Exception {
		return setup();
	}

	/**
	 * @param topic - The topic the message was published in.
	 * @param payload - The actual message.
	 * @return if the command is a disconnect query.
	 */
	public boolean parseAndRun(String topic, String payload){
		System.out.println(topic+","+payload+" getting parsed");
		for(Command c : commands){
            if (c.isTopic(topic)) {
                String[] args = payload.split(",");
                if(!c.onTrigger(topic, args)){
		            c.defaultTrigger(topic, args);
                }
		        break;
            }
        }
		return false;
	}

	private void run(){

	}
}
