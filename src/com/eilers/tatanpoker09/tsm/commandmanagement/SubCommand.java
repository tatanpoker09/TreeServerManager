package com.eilers.tatanpoker09.tsm.commandmanagement;

public class SubCommand implements Command{
	private String name;
	private String[] args;
	
	public SubCommand(String name) {
		this.name = name;
	}

	@Override
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
}