package com.dd.guessinggame.networking.server;

import com.dd.guessinggame.networking.utils.Handler;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is used only in the server to handle the communications with the client
 * @author d471061c
 */
public class Session {
    
        private Handler handler;
        private ArrayList<String> words;
        
        public Session (Socket client) throws IOException {
            this.handler = new Handler(client);
            this.words = new ArrayList();
            words.add("a");
            words.add("b");
        }
        
        /**
         * Handle command given from the client
         * @param command Command in string format
         */
        public void handleCommand(String command) {
            if (command.equals("hello")) {
                handler.send("hello!");
            }
            else if (command.equals("start")) {
                int a = (int) Math.floor(Math.random()*2);
                handler.send(words.get(a));
            } else {
                handler.send("hi!");
            }
        }
        
        /**
         * Serve the client
         */
        public void serve() {
            while (true) {
                try {
                    String command = this.handler.readLine();
                    if (command == null) break;
                    this.handleCommand(command);
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    break;
                }
            }
        }
        
        /**
         * Send message to the client
         * @param message Message to be sent
         */
        public void send(String message) {
            this.handler.send(message);
        }
}
