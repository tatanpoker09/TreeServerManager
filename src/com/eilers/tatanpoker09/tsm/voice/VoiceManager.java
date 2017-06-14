package com.eilers.tatanpoker09.tsm.voice;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.server.Tree;

public class VoiceManager extends Thread{
	private boolean active;
	private static final String KEY = "tree";
	
	private List<VoiceCommand> voiceCommands;

	public VoiceManager() {
		Logger log = Tree.getLog();
		log.info("Starting VoiceManager");
		active = true;
		voiceCommands = new ArrayList<VoiceCommand>();
	}

	public void setup() {
		//TODO Work with CMU Sphinx4.
	}
	
	public void recognize() {
		Logger log = Tree.getLog();
		log.info("Starting Recognizer.");
		log.info("Stopping recognizer");
	}
}