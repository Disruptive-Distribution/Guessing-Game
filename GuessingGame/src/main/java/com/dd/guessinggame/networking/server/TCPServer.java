package com.dd.guessinggame.networking.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author d471061c
 */
public class TCPServer {
    
    private ServerSocket socket; //the socket where the client connects to
    private int port; // the port where the socket listens to, 2000 as default
    public ArrayList<ClientThread> clients; //list of clients currently connected
    public Game game;

    public TCPServer(int port) {
        this.port = port;
        this.clients = new ArrayList();
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
            try {
                Socket client = this.socket.accept();
                System.out.println("[*] Client accepted!");
                //  Add the client to the list
                ClientThread connected = new ClientThread(client);
                clients.add(connected);
                connected.start();
            } catch (IOException ex) {
                System.err.println("[!] Error: " + ex.getMessage());
            }        
        }
    }
    public void beginGame() {
        this.game = new Game(clients);
        this.game.play(); //I think this blocks everything now, we may need another server thread to run the game
        this.game = null;
    }
    //------------------------------------------------------------------------//
    private class ClientThread extends Thread {
        private Socket client;
        private PrintWriter out;
        private BufferedReader in;
        private boolean inGame;
        
        public ClientThread(Socket sock) throws IOException {
            this.client = sock; 
            this.out = new PrintWriter(client.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            inGame = false;
        }

        public void run() {
            while(true){
                try {
                    String command = in.readLine();
                    if (command == null) break;
                    if(inGame) {
                        handleGameMessages(command);
                    }
                    this.handleCommand(command);
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    break;
                }
            }
        }
        //method for handling the game calls, should come here if game has started
        public void handleGameMessages(String clientMessage) {
            if(clientMessage.equals("correct")) {
                
            }
        }
        /*public boolean checkIfReady() {
            boolean rdy = false;
            send("starting");
            try {
                while(readLine() != null) {
                    if(readLine().equals("ready")) {
                        rdy = true;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return rdy;
        } */
        
        public void handleCommand(String command) {
            if (command.equals("hello")) {
                send("hello!");
            }
            else if (command.equals("start")) {
                if(game == null) {
                    beginGame();
                } else {
                    send("Game currently going, please wait till next round.");
                }
            }
            else if (command.equals("ready")) {
                send("Game is starting!");
                
            } else {
                send("hi!");
            }
        }
        private void send(String message) {
            this.out.println(message);
        }
        private String readLine() throws IOException {
            return this.in.readLine();
        }
    }
    //------------------------------------------------------------------------//
    
    private class Game {
        private ArrayList<String> words;
        private ArrayList<ClientThread> players;
        
        public Game(ArrayList<ClientThread> players) {
            this.players = players;
            this.words = new ArrayList<>();
            words.add("a");
            words.add("b");
        }
        //at the moment just sends 2 messages to all players
        public void play() {
            //confirmReadyPlayers();
            for (int i = 0; i < 2; i++) {
                int a = (int) Math.floor(Math.random()*2);
                multicast(words.get(a));
            }
        }
        //
        /*private void confirmReadyPlayers() {
            for(ClientThread player: players) {
                    if(!player.checkIfReady()) {
                        players.remove(player);
                    }
                }
        } */
        private void multicast(String msg) {
            for(ClientThread player : players) {
                    player.send(msg);
                }
        }
    }
}
