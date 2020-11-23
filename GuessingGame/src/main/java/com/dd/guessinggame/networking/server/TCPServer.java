package com.dd.guessinggame.networking.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    public HashMap<String, ClientThread> clients; //list of clients currently connected
    public Game game;
    private final String INFO = "Welcome New Player!\rHow to play:\r-Write 'start' to begin a game, you may have to wait for\r the ongoing game to end first"
            + " (but don't worry you get informed when it's over!)"
            + "\r-Write the given word faster than other players and get a point"
            + "\r-Win the most rounds and win the game! \r Good luck!";

    public TCPServer(int port) {
        this.port = port;
        this.clients = new HashMap();
        this.game = new Game(this);
    }

    /**
     * Bind the port for the server
     *
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
        for (ClientThread player : clients.values()) {
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
                ClientThread connected = new ClientThread(this, client);
                String id = UUID.randomUUID().toString();
                clients.put(id, connected);
                connected.setID(id);
                connected.start();
                connected.send(this.INFO);

                if (game.isRunning()) {
                    connected.send("There's a game going on");
                }
            } catch (IOException ex) {
                System.err.println("[!] Error: " + ex.getMessage());
            }
        }
    }

    public void beginGame() {
        HashMap<String, ClientThread> currentPlayers = new HashMap<>();
        currentPlayers.putAll(clients);
        this.game.addPlayers(currentPlayers);
        this.game.startSession();
    }
    
    public boolean gameIsRunning() {
        return game != null;
    }
    
    public void stopGame() {
        
    }
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public HashMap<String, ClientThread> getClients() {
        return clients;
    }

    public void setClients(HashMap<String, ClientThread> clients) {
        this.clients = clients;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    //------------------------------------------------------------------------//
    public class Game {

        private ArrayList<String> words;
        private HashMap<String, ClientThread> players;
        private HashMap<String, Integer> points;
        private TCPServer server;
        private boolean running;
        private String current;
        
        public Game(TCPServer server) {
            this.server = server;
            this.running = false;
            this.words = new ArrayList<>();
            this.points = new HashMap<>();
            words.add("a");
            words.add("b");
        }
        public void addPlayers(HashMap<String, ClientThread> players) {
            this.players = players;
            for (String id : players.keySet()) {
                points.put(id, 0);
            }
        }
        
        public void reset() {
            this.running = false;
            this.players.clear();
            this.points.clear();
        }
        
        public boolean inGame(String id) {
            return players.containsKey(id);
        }
        
        //at the moment just sends 7 messages to all players, sleeps 8 sec between
        public void play() {
            confirmReadyPlayers();
            running = true;
            for (int i = 0; i < 4; i++) {
                int a = (int) Math.floor(Math.random() * 2);
                this.current = words.get(a);
                multicastPlayers(current);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            multicastWinner();
            reset();
            this.server.multicast("The game has ended.");    
        }
        public boolean isRunning() {
            return this.running;
        }
        private void confirmReadyPlayers() {
            for (ClientThread player : players.values()) {
                player.send("New game starting");
            }
        }

        private void multicastPlayers(String msg) {
            for (ClientThread player : players.values()) {
                player.send(msg);
            }
        }
        public void startSession() {
            GameSession session = new GameSession(game);
            session.start();
        }
        public void guess(String id, String guess) {
            if(guess.equals(current) && points.keySet().contains(id)) {
                points.put(id, points.get(id)+1);   
            }
        }
        public String getWinner() {
            String winner = "";
            int max = 0;
            for (Map.Entry<String, Integer> entry : points.entrySet()) {
                if(entry.getValue() > max) {
                    winner = entry.getKey();
                    max = entry.getValue();
                }
            }
            return winner;
        }
        public void multicastWinner() {
            String winner = getWinner();
            for (Map.Entry<String, ClientThread> entry : players.entrySet()) {
                if(entry.getKey().equals(winner)) {
                    entry.getValue().send("You won!");
                } else {
                    entry.getValue().send("You lost! Better luck next time!");
                }
            }
        }
        private class GameSession extends Thread {
            
            private Game game;
            
            public GameSession(Game game) {
                this.game = game;
            }

            @Override
            public void run() {
                this.game.play();
            }
            
        }
    }
}
