package com.eilers.tatanpoker09.tsm.client;

import java.net.InetAddress;

/**
 * Represents a Client Connection, handles the connection with a single client.
 * @author tatanpoker09
 *
 */
public class TreeClient extends Thread {
	private String user;
	private InetAddress ip;
	
	public TreeClient() {
		super();
	}
}
