package com.eilers.tatanpoker09.tsm.commandmanagement;

import java.util.List;

public class BaseCommand implements Command{
	private List<SubCommand> subCommands;
	
	public BaseCommand() {
		
	}
	
	
	@Override
	public void onTrigger(String[] args) {
		
	}

}
