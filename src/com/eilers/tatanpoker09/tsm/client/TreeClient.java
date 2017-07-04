package com.eilers.tatanpoker09.tsm.client;

import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import com.eilers.tatanpoker09.tsm.server.Tree;

import java.io.*;
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
            sendMessage("connected");
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

    public void sendMessage(String message) throws IOException {
        byte[] messageBytes = (message+"1").getBytes();
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

        dOut.writeInt(messageBytes.length); // write length of the message
        System.out.println(messageBytes.length);
        dOut.write(messageBytes);
    }
}
