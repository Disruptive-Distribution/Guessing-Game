/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dd.guessinggame.networking.server.game;

import com.dd.guessinggame.networking.server.ClientThread;
import com.dd.guessinggame.networking.server.TCPServer;
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
        GameSession session = new GameSession(this);
        session.start();
    }

    public void guess(String id, String guess) {
        if (guess.equals(current) && points.keySet().contains(id)) {
            points.put(id, points.get(id) + 1);
            current = null;
        }
    }

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
