/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    public void send(String message) {
        this.out.println(message);
    }
    
    public String readLine() throws IOException {
        return this.in.readLine();
    }
    
}
