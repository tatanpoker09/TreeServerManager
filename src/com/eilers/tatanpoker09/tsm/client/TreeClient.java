package com.eilers.tatanpoker09.tsm.client;

import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import com.eilers.tatanpoker09.tsm.server.Tree;

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
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine, outputLine;

            System.out.println("Entering listener loop for: "+socket.getInetAddress());
            CommandManager commandManager = Tree.getServer().getcManager();
            while ((inputLine = in.readLine()) != null) {
                boolean disconnect = commandManager.parseAndRun(inputLine, socket.getInetAddress());
                if (disconnect)
                    break;
            }
            System.out.println("Client: "+socket.getInetAddress()+" has disconnected from the server.");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
