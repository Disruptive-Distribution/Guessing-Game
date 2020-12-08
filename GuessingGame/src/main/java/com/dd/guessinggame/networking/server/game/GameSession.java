/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dd.guessinggame.networking.server.game;

/**
 * A new thread where the game is run
 * 
 * @author Sade-Tuuli
 */
public class GameSession extends Thread {

    private Game game;

    public GameSession(Game game) {
        this.game = game;
    }

    /**
     * Run the game
     */
    @Override
    public void run() {
        this.game.play();
    }
}
