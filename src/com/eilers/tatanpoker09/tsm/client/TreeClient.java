package com.eilers.tatanpoker09.tsm.client;

import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import net.sf.xenqtt.client.*;
import net.sf.xenqtt.message.ConnectReturnCode;
import net.sf.xenqtt.message.QoS;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a Client Connection, handles the connection with a single client.
 * @author tatanpoker09
 *
 */
public class TreeClient extends Thread {
	private String user;
	private String ip;
	private final String port = "7727";
	
	public TreeClient(String ip) {
	    super();
	    this.ip = ip;
	}

	@Override
    public void run(){

	}

    public void sendMessage(String message) throws IOException {
    }
}