package com.dd.guessinggame;

import com.dd.guessinggame.networking.client.ClientGUI;
import com.dd.guessinggame.networking.server.TCPServer;

public class Main {

    public static final int DEFAULT_PORT = 2000;

    /**
     * Run server on given port
     *
     * @param port Port number
     */
    public static void runServer(int port) {
        TCPServer server = new TCPServer(port);
        boolean running = server.bind();
        
        if (running) {
            System.out.println("[*] Hosting server on localhost:" + port);
            server.mainloop();
        } else {
            System.out.println("Failed to bind port " + port);
        }
    }

    public static void main(String[] args) {
        // handleTerminalArguments(args);
        //runServer(2000);
        //runClient("localhost",2000);
        ClientGUI.main(args);
    }

}
