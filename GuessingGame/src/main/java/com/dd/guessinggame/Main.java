package com.dd.guessinggame;

import com.dd.guessinggame.networking.client.ClientGUI;
import com.dd.guessinggame.networking.client.TCPClient;
import com.dd.guessinggame.networking.server.TCPServer;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    public static final int DEFAULT_PORT = 2000;

    /**
     * Run the client
     *
     * @param ipAddress IP address of the server
     * @param port Port of the client
     */
    public static void runClient(String ipAddress, int port) {
        TCPClient client = new TCPClient();
        try {
            client.connect(ipAddress, port);
            client.mainloop();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

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

    /**
     * Handle arguments from terminal
     *
     * @param args List of arguments
     */
    public static void handleTerminalArguments(String[] args) {
        // Used to parse command line
        CommandLineParser parser = new DefaultParser();

        // Used to display help and errors from the parsing
        HelpFormatter formatter = new HelpFormatter();

        // Options
        Options options = new Options();

        // Global options
        Option helpOption = new Option("h", "help", false, "Show this help message ");

        // Client options
        Option clientOption = new Option("c", "connect", true, "Connect to server");

        // Server options
        Option portOption = new Option("p", "port", true, "Port for the server, defaults to 2000");

        options.addOption(helpOption);
        options.addOption(clientOption);
        options.addOption(portOption);

        try {
            CommandLine commands = parser.parse(options, args);

            // display help
            if (commands.hasOption("help")) {
                formatter.printHelp("GuessingGame", options);
                System.exit(1);
            }

            if (commands.hasOption("connect")) {
                // Handle client
                // Verify the address to be the correct form and parse it.
                String serverAddress = commands.getOptionValue("connect");
                if (!serverAddress.matches("[^\\:]+:[0-9]+")) {
                    throw new ParseException("Host must be in form of 'ip:port'");
                }
                String data[] = serverAddress.split(":");
                runClient(data[0], Integer.parseInt(data[1]));
            } else {
                // Handle server
                if (commands.hasOption("port")) {
                    if (!commands.getOptionValue("port").matches("[0-9]+")) {
                        throw new ParseException("Port must be a number");
                    }
                    int portNumber = Integer.parseInt(commands.getOptionValue("port"));
                    runServer(portNumber);
                } else {
                    runServer(DEFAULT_PORT);
                }
            }
        } catch (ParseException e) {
            // Display error if there's missing parameters
            // or some parameters don't match.
            System.out.println(e.getMessage() + "\n");
            formatter.printHelp("GuessingGame", options);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // handleTerminalArguments(args);
        //runServer(2000);
        //runClient("localhost",2000);
        ClientGUI.main(args);
    }

}
