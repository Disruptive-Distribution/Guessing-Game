/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    public boolean bind() {
        try {
            this.socket = new ServerSocket(port);
            return true;
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return false;
    }
    
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
