/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dd.guessinggame.networking.server.game;

import com.dd.guessinggame.networking.server.ClientThread;
import com.dd.guessinggame.networking.server.GameServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sade-Tuuli
 */
public class Game {

    private ArrayList<String> words;
    private HashMap<String, ClientThread> players;
    private HashMap<String, Integer> points;
    private GameServer server;
    private boolean running;
    private String current;
    private boolean guessed;

    public Game(GameServer server) {
        this.server = server;
        this.running = false;
        this.words = new ArrayList<>();
        this.points = new HashMap<>();
        words.add("a");
        words.add("b");
    }

    /**
     * Add a list of players for the game
     * @param players List of players that will be playing
     */
    public void addPlayers(HashMap<String, ClientThread> players) {
        this.players = players;
        for (String id : players.keySet()) {
            points.put(id, 0);
        }
    }

    /**
     * Clear all the information regarding the game
     */
    public void reset() {
        this.running = false;
        this.players.clear();
        this.points.clear();
    }

    /**
     * Return information if the particular player is in the game or not
     * @param id player ID
     * @return
     */
    public boolean inGame(String id) {
        return players.containsKey(id);
    }

    /**
     * Loop that runs the game
     */
    public void play() {
        confirmReadyPlayers();
        running = true;
        for (int i = 0; i < 4; i++) {
            guessed = false;
            int a = (int) Math.floor(Math.random() * 2);
            this.current = words.get(a);
            multicastPlayers(current);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        multicastWinner();
        reset();
        this.server.multicast("The game has ended.");
    }

    /**
     * Check if there's a game running
     * @return
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Inform the players of a starting game
     */
    private void confirmReadyPlayers() {
        for (ClientThread player : players.values()) {
            player.send("New game starting");
        }
    }

    /**
     * Multicast a message to the players
     */
    private void multicastPlayers(String msg) {
        for (ClientThread player : players.values()) {
            player.send(msg);
        }
    }

    /**
     * Start a game session
     */
    public void startSession() {
        GameSession session = new GameSession(this);
        session.start();
    }

    /**
     * Handle a guess made by a player
     * @param id the player ID
     * @param guess the word that was guessed
     */
    public synchronized void guess(String id, String guess) {
        if (guess.equals(current) && points.keySet().contains(id) && !guessed) {
            points.put(id, points.get(id) + 1);
            guessed = true;
        }
        if(guess.equals(current)) {
            players.get(id).send("Correct!");
        } else {
            players.get(id).send("Incorrect!");
            
        }
    }

    /**
     * Check which of the players has the biggest score
     * @return The ID of the player with the biggest score
     */
    public String getWinner() {
        String winner = "";
        int max = 0;
        for (Map.Entry<String, Integer> entry : points.entrySet()) {
            if (entry.getValue() > max) {
                winner = entry.getKey();
                max = entry.getValue();
            }
        }
        return winner;
    }

    /**
     * Tell all the players if they've won or not
     */
    public void multicastWinner() {
        String winner = getWinner();
        for (Map.Entry<String, ClientThread> entry : players.entrySet()) {
            if (entry.getKey().equals(winner)) {
                entry.getValue().send("You won!");
            } else {
                entry.getValue().send("You lost! Better luck next time!");
            }
        }
    }
}
