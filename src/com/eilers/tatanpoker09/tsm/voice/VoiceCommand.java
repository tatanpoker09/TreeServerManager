package com.eilers.tatanpoker09.tsm.voice;

import java.io.File;
import java.util.Set;

public class VoiceCommand {
	private Set<String> phrases;
	private Runnable trigger;
	
	protected VoiceCommand(File file, Runnable trigger) {
		this.phrases = loadFile(file);
		this.trigger = trigger;
	}
}
