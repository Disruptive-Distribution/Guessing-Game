package com.dd.guessinggame.networking.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author d471061c
 */
public class TCPServer {
    
    private ServerSocket socket; //the socket where the client connects to
    private int port; // the port where the socket listens to, 2000 as default
    private ArrayList<ClientThread> clients; //list of clients currently connected

    public TCPServer(int port) {
        this.port = port;
        this.clients = new ArrayList();
    }
    
    /**
     * Broadcast a message
     * @param message Message
     */
    public void broadcast(String message) {
        for (ClientThread client: clients) {
            client.sendMessage(message);
        }
    }
    
    /**
     * Bind the port for the server
     * @return true if the binding was successful, false if not
     */
    public boolean bind() {
        try {
            this.socket = new ServerSocket(port);
            return true;
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return false;
    }
    
    /**
     * Handles the connections of the incoming clients
     */
    public void mainloop() {
        while (true) {
            try {
                Socket client = this.socket.accept();
                System.out.println("[*] Client accepted!");
                //  Add the client to the list
                ClientThread connected = new ClientThread(client);
                clients.add(connected);
                connected.start();
            } catch (IOException ex) {
                System.err.println("[!] Error: " + ex.getMessage());
            }        
        }
    }
    
    
}
