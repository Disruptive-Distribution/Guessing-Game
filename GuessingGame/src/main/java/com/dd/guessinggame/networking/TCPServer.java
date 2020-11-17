package com.dd.guessinggame.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author d471061c
 */
public class TCPServer {
    
    private ServerSocket socket; //the socet where the client connects to
    private int port; // the port where the socket listens to, 2000 as default
    private ArrayList<ClientThread> clients; //list of clients currently connected

    public TCPServer(int port) {
        this.port = port;
        clients = new ArrayList<>();
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
            System.out.println("[*] Waiting for client...");
            try {
                Socket client = this.socket.accept();
                // Handle clients here
                //
                //create a new thread
                ClientThread connected = new ClientThread(client);
                connected.start();
                //
                System.out.println("[*] Client accepted!");
                //Add client to the list
                clients.add(connected);
                System.out.println("[*] New client added to the list, now " + clients.size());
            } catch (IOException ex) {
                System.err.println("[!] Error: " + ex.getMessage());
            }        
        }
    }
    public void messageAllThreads(String msg) {
        for (ClientThread client : clients) {
            client.getSession().send(msg);
        }
    }
    
}
