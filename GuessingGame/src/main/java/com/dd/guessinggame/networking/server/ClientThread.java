/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dd.guessinggame.networking.server;

import com.dd.guessinggame.networking.server.TCPServer;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sade-Tuuli
 */
public class ClientThread extends Thread {
    
    private Socket client;
    private Session session;

    public ClientThread(Socket sock) {
        this.client = sock; 
    }
    
    public void run() {
        try {
            this.session = new Session(client);
            this.session.serve();
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Send message to the client
     * @param message message
     */
    public void sendMessage(String message) {
        this.session.send(message);
    }
    
    public Session getSession() {
        return session;
    }
}
