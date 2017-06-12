package com.eilers.tatanpoker09.tsm.voice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.darkprograms.speech.recognizer.Recognizer;
import com.eilers.tatanpoker09.tsm.server.Tree;

import javaFlacEncoder.FLACFileWriter;

public class VoiceManager extends Thread{
	private boolean active;
	private static final String KEY = "tree";
	private Recognizer recognizer;
	
	private List<VoiceCommand> voiceCommands;

	public VoiceManager() {
		Logger log = Tree.getLog();
		log.info("Starting VoiceManager");
		this.recognizer = recognizer;
		active = true;
		voiceCommands = new ArrayList<VoiceCommand>();
	}

	public void setup() {
		//TODO FIND A WAY TO ENCRYPT THIS SO THAT ONLY I CAN SEE AND KNOW IT.
		Recognizer recognizer = new Recognizer (Recognizer.Languages.ENGLISH_US, "AIzaSyAMb8u4N6oLq_-HlU50JDR9aHC_lnu8wIQ");
		
		
	}
	
	public void recognize() {
		Logger log = Tree.getLog();
		log.info("Starting Recognizer");
		AudioFileFormat.Type[] typeArray = AudioSystem.getAudioFileTypes();
		for(AudioFileFormat.Type type : typeArray) {
			System.out.println("type: " + type.toString());
		} 

		Microphone mic = new Microphone(FLACFileWriter.FLAC);
		File file = new File ("test.mp3");	//Name your file whatever you want
		try {
			mic.captureAudioToFile (file);
		} catch (Exception ex) {
			//Microphone not available or some other error.
			System.out.println ("ERROR: Microphone is not availible.");
			active = false;
			ex.printStackTrace ();
			mic.close();
			return;
		}
		/* User records the voice here. Microphone starts a separate thread so do whatever you want
		 * in the mean time. Show a recording icon or whatever.
		 */
		try {
			System.out.println ("Recording...");
			Thread.sleep (5000);	//In our case, we'll just wait 10 seconds.
			mic.close ();
		} catch (InterruptedException ex) {
			ex.printStackTrace ();
		}

		mic.close ();		//Ends recording and frees the resources
		System.out.println ("Recording stopped.");
		//Although auto-detect is available, it is recommended you select your region for added accuracy.
		try {
			int maxNumOfResponses = 4;
			System.out.println("Sample rate is: " + (int) mic.getAudioFormat().getSampleRate());
			GoogleResponse response = recognizer.getRecognizedDataForFlac (file, maxNumOfResponses, (int) mic.getAudioFormat().getSampleRate ());
			System.out.println ("Google Response: " + response.getResponse ());
			System.out.println ("Google is " + Double.parseDouble (response.getConfidence ()) * 100 + "% confident in" + " the reply");
			System.out.println ("Other Possible responses are: ");
			for (String s:response.getOtherPossibleResponses ()) {
				System.out.println ("\t" + s);
			}
		}
		catch (Exception ex) {
			// TODO Handle how to respond if Google cannot be contacted
			System.out.println ("ERROR: Google cannot be contacted");
			ex.printStackTrace ();
		}

		file.deleteOnExit ();	//Deletes the file as it is no longer necessary.
		log.info("Stopping recognizer");
	}
}