package com.dd.guessinggame.networking.utils;

import java.io.IOException;
import java.net.Socket;

/**
 * This class is used only in the server to handle the communications with the client
 * @author d471061c
 */
public class Session {
    
        private Handler handler;
        
        public Session (Socket client) throws IOException {
            this.handler = new Handler(client);
        }
        
        /**
         * Handle command given from the client
         * @param command Command in string format
         */
        public void handleCommand(String command) {
            if (command.equals("hello")) {
                handler.send("hello!");
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
}
