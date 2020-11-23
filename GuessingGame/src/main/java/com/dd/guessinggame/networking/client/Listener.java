/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dd.guessinggame.networking.client;

import com.dd.guessinggame.networking.utils.Handler;
import java.io.IOException;
import javax.swing.JTextArea;

/**
 * Listen to the inputs from the server and write them to the GUI
 *
 * @author d471061c
 */
public class Listener extends Thread {

    private Handler handler;
    private JTextArea area;

    public Listener(Handler handler, JTextArea area) {
        this.handler = handler;
        this.area = area;
    }

    @Override
    public void run() {
        String output;
        while (true) {
            try {
                output = handler.readLine();
                // We might be sending messages to the server and writing those down
                // to the JTextArea. For that reason this is synchronized
                synchronized (this.area) {
                    this.area.setText(this.area.getText() + output + "\n");
                }
            } catch (IOException ex) {
                break;
            }
        }
    }

}
