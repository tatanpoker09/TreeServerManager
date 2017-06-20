package com.eilers.tatanpoker09.tsm.voice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.server.Tree;

public class VoiceManager implements Callable, Manager {
	private boolean active;
	private static final String KEY = "tree";
	
	private List<VoiceCommand> voiceCommands;

	public VoiceManager() {
		Logger log = Tree.getLog();
		log.info("Starting VoiceManager");
		active = true;
		voiceCommands = new ArrayList<VoiceCommand>();
	}

	public boolean setup() {
		//TODO Work with CMU Sphinx4.
        LightVoiceCommand lvc = new LightVoiceCommand(new File("resources/voicecommands/lights-on.yml"));
		return true;
	}

	public void postSetup() {

	}

	public void recognize() {
		Logger log = Tree.getLog();
		log.info("Starting Recognizer.");
		log.info("Stopping recognizer");
	}

	public Boolean call() throws Exception {
		return setup();
	}
}