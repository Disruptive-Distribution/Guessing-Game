package com.dd.guessinggame.networking;

import com.dd.guessinggame.networking.utils.Session;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author d471061c
 */
public class TCPServer {
    
    private ServerSocket socket;
    private int port;

    public TCPServer(int port) {
        this.port = port;
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
                System.out.println("[*] Client accepted!");
                Session session = new Session(client);
                session.serve();
            } catch (IOException ex) {
                System.err.println("[!] Error: " + ex.getMessage());
            }
        }
    }
}
