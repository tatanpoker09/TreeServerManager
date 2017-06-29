package com.eilers.tatanpoker09.tsm.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Represents a Client Connection, handles the connection with a single client.
 * @author tatanpoker09
 *
 */
public class TreeClient extends Thread {
	private String user;
	private Socket socket;
	
	public TreeClient(Socket clientSocket) {
	    super();
        this.socket = clientSocket;
	}

	@Override
    public void run(){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.write("[tree]connected");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
