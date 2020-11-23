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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            if(!server.game.isRunning()) {
                if(command.equals("start")) {
                    server.beginGame();
                }
            } else {
                if(server.getGame().inGame(id)) {
                    //handle playing
                } else {
                    send("Game running, please wait until next round.");
                }
            }
            /*if (command.equals("start")) {
                if (!server.gameIsRunning()) {
                    server.beginGame();
                } else {
                    send("Game currently going, please wait till next round.");
                }
            } else {
                send("Invalid command.");
            } */
        }

        public void send(String message) {
            this.out.println(message);
        }

        //not in use yet
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

        private String readLine() throws IOException {
            return this.in.readLine();
        }
    }