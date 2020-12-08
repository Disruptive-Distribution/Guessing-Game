package com.dd.guessinggame.networking.server;

import com.dd.guessinggame.networking.server.game.Game;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author d471061c
 */
public class GameServer {

    private ServerSocket socket; //the socket where the client connects to
    private int port; // the port where the socket listens to, 2000 as default
    public HashMap<String, ClientThread> clients; //list of clients currently connected
    public Game game;
    private final String INFO = "Welcome New Player!\rHow to play:\r-Write 'start' to begin a game, you may have to wait for\r the ongoing game to end first"
            + " (but don't worry you get informed when it's over!)"
            + "\r-Write the given word faster than other players and get a point"
            + "\r-Win the most rounds and win the game! \r Good luck!";

    public GameServer(int port) {
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

    public void multicast(String msg) {
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

    /**
     * Begin the game
     */
    public void beginGame() {
        HashMap<String, ClientThread> currentPlayers = new HashMap<>();
        currentPlayers.putAll(clients);
        this.game.addPlayers(currentPlayers);
        this.game.startSession();
    }

    /**
     * Check if the game is running
     * @return True if the game is on
     */
    public boolean gameIsRunning() {
        return game != null;
    }

    /**
     * Stop the on going game
     */
    public void stopGame() {
        this.game = null;
    }

    /**
     * Get port in which the server is running on
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Set port for the server
     * @param port Port number
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get list of clients
     * @return List of clients and their ids in hashmap
     */
    public HashMap<String, ClientThread> getClients() {
        return clients;
    }

    /**
     * Set client
     * @param clients 
     */
    public void setClients(HashMap<String, ClientThread> clients) {
        this.clients = clients;
    }

    /**
     * Get game from the 
     * @return 
     */
    public Game getGame() {
        return game;
    }

    /**
     * Set game to the server
     * @param game Game that is played 
     */
    public void setGame(Game game) {
        this.game = game;
    }
}
