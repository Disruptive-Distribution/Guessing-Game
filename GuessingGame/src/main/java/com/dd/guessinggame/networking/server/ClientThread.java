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

    public void send(String message) {
        this.out.println(message);
    }

    private String readLine() throws IOException {
        return this.in.readLine();
    }
}
