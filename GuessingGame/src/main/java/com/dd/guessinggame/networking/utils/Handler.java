package com.dd.guessinggame.networking.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author d471061c
 */
public class Handler {
        
    private PrintWriter out;
    private BufferedReader in;
    
    /**
     * Initialize handler
     * @param client Socket to read and write to
     * @throws IOException If the error occurs
     */
    public Handler(Socket client) throws IOException {
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }    
    
    /**
     * Send a message to the client/server through socket
     * @param message Message to be sent
     */
    public void send(String message) {
        this.out.println(message);
    }
    
    /**
     * Read next line from the client/server    
     * @return Message from the client/server
     * @throws IOException If the server/client disconnects
     */
    public String readLine() throws IOException {
        return this.in.readLine();
    }
    
}
