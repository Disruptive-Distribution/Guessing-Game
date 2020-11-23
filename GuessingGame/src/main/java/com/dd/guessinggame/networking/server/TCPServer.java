package com.dd.guessinggame.networking.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author d471061c
 */
public class TCPServer {
    
    private ServerSocket socket; //the socket where the client connects to
    private int port; // the port where the socket listens to, 2000 as default
    public HashMap<UUID, ClientThread> clients; //list of clients currently connected
    public Game game;
    private UUID uid;
    private String info;

    public TCPServer(int port) {
        this.port = port;
        this.clients = new HashMap();
        this.info = "Welcome New Player!\rHow to play:\r-Write 'start' to begin a game, you may have to wait for\r the ongoing game to end first"
                + " (but don't worry you get informed when it's over!)"
                + "\r-Write the given word faster than other players and get a point"
                + "\r-Win the most rounds and win the game! \r Good luck!";
        this.uid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
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
    private void multicast(String msg) {
            for(ClientThread player : clients.values()) {
                    player.send(msg);
                }
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
                //clients.add(connected);
                UUID id = uid.randomUUID();
                while(clients.containsKey(id)) {
                    id = uid.randomUUID();
                }
                clients.put(id, connected);
                connected.start();
                connected.send(info);
                
                if(game != null) {
                    connected.send("There's a game going on");
                }
            } catch (IOException ex) {
                System.err.println("[!] Error: " + ex.getMessage());
            }        
        }
    }
    public void beginGame() {
        HashMap<UUID, ClientThread> currentPlayers = new HashMap<>();
        currentPlayers.putAll(clients);
        this.game = new Game(currentPlayers);
        this.game.play();
        this.game = null;
        multicast("Game has ended.");
    }
    //------------------------------------------------------------------------//
    private class ClientThread extends Thread {
        private Socket client;
        private PrintWriter out;
        private BufferedReader in;
        private boolean inGame;
        private UUID ID;
        
        public ClientThread(Socket sock) throws IOException {
            this.client = sock; 
            this.out = new PrintWriter(client.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.ID = null;
            inGame = false;
        }
        public void setID(UUID id) {
            this.ID = id;
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
            
        }
        
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
        //not in use yet
        private void sendAndExpect(String message) {
            this.out.println(message);
            try {
                String command = in.readLine();
                
                if(command.equals(message)) {
                    this.out.println("Correct");
                } else {
                    this.out.println("");
                }
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        private String readLine() throws IOException {
            return this.in.readLine();
        }
    }
    //------------------------------------------------------------------------//
    
    private class Game {
        private ArrayList<String> words;
        private HashMap<UUID, ClientThread> players;
        
        public Game(HashMap<UUID, ClientThread> players) {
            this.players = players;
            this.words = new ArrayList<>();
            words.add("a");
            words.add("b");
        }
        //at the moment just sends 7 messages to all players, sleeps 8 sec between
        public void play() {
            confirmReadyPlayers();
            for (int i = 0; i < 7; i++) {
                int a = (int) Math.floor(Math.random()*2);
                multicastPlayers(words.get(a));
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        //
        private void confirmReadyPlayers() {
            for(ClientThread player : players.values()) {
                    player.send("New game starting");
                }
        }
        private void multicastPlayers(String msg) {
            for(ClientThread player : players.values()) {
                    player.send(msg);
                }
        }
    }
}
