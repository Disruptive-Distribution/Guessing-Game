/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dd.guessinggame.networking.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Sade-Tuuli
 */
public class ClientThread extends Thread {

    private TCPServer server;
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    private boolean inGame;
    private String id;

    public ClientThread(TCPServer server, Socket sock) throws IOException {
        this.server = server;
        this.client = sock;
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.id = null;
        inGame = false;
    }

    /**
     * Set identifier for the client
     * @param id 
     */
    public void setID(String id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String command = in.readLine();
                if (command == null) {
                    break;
                }
                this.handleCommand(command);
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
                break;
            }
        }
    }

    /**
     * Handle command given from client
     * @param command Command from the client
     */
    public void handleCommand(String command) {
        if (!server.game.isRunning()) {
            if (command.equals("start")) {
                server.beginGame();
            } else {
                send("Unknown command.");
            }
        } else {
            if (server.getGame().inGame(id)) {
                //handle playing
                server.getGame().guess(id, command);
            } else {
                send("Game running, please wait until next round.");
            }
        }
    }

    /**
     * Send message to the server
     * @param message Message to be sent
     */
    public void send(String message) {
        this.out.println(message);
    }

    /**
     * Send message and expect a result
     * @param message Message to be sent 
     */
    public void sendAndExpect(String message) {
        this.out.println(message);
        try {
            String command = in.readLine();

            if (command.equals(message)) {
                this.out.println("Correct");
            } else {
                this.out.println("Incorrect");
            }
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Read line from the input stream
     * @return String if the reading was successful
     * @throws IOException If the client disconnects all of the sudden
     */
    private String readLine() throws IOException {
        return this.in.readLine();
    }
}
