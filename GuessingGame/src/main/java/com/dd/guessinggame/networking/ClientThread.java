/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dd.guessinggame.networking;

import com.dd.guessinggame.networking.utils.Session;
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
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Ok session!");
        session.serve();
    }
    public Session getSession() {
        return session;
    }
}
