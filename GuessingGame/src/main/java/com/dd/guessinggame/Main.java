package com.dd.guessinggame;

import com.dd.guessinggame.networking.server.GameServer;
import com.dd.guessinggame.utils.ConsoleUtils;

public class Main {

    public static final int DEFAULT_PORT = 2000;

    /**
     * Run server on given port
     *
     * @param port Port number
     */
    public static void runServer(int port) {
        GameServer server = new GameServer(port);
        boolean running = server.bind();

        if (running) {
            System.out.println("[*] Hosting server on localhost:" + port);
            server.mainloop();
        } else {
            System.out.println("Failed to bind port " + port);
        }
    }

    public static void main(String[] args) {
        ConsoleUtils.handleTerminalArguments(args);
    }

}
