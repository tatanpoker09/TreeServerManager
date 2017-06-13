package com.eilers.tatanpoker09.tsm.voice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.server.Tree;

public class VoiceCommand {
	private Set<String> phrases;
	private Runnable trigger;
	
	protected VoiceCommand(File file, Runnable trigger) {
		this.phrases = loadFile(file);
		this.trigger = trigger;
	}

	private Set<String> loadFile(File file) {
		Set<String> phrases = new HashSet<String>();
		Logger log = Tree.getLog();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.severe("Error loading: "+file.getName()+" as the specified file does not exist!");
			return phrases;
		}
		BufferedReader bReader = new BufferedReader(fr);
		String line = null;
		do {
			try {
				line = bReader.readLine();
				phrases.add(line);
			} catch (IOException e) {
				e.printStackTrace();
				log.severe("Error reading line");
				line=null;
			}
			
		} while(line!=null);
		try {
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("Error closing VoiceCommand reading buffer");
		}
		return phrases;
	}
}
