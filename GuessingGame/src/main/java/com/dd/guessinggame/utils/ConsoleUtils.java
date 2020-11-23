/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dd.guessinggame.utils;

import static com.dd.guessinggame.Main.DEFAULT_PORT;
import static com.dd.guessinggame.Main.runServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Sade-Tuuli
 */
public class ConsoleUtils {
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

        // Server options
        Option portOption = new Option("p", "port", true, "Port for the server, defaults to 2000");

        options.addOption(helpOption);
        options.addOption(portOption);

        try {
            CommandLine commands = parser.parse(options, args);

            // display help
            if (commands.hasOption("help")) {
                formatter.printHelp("GuessingGame", options);
                System.exit(1);
            }
            
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

        } catch (ParseException e) {
            // Display error if there's missing parameters
            // or some parameters don't match.
            System.out.println(e.getMessage() + "\n");
            formatter.printHelp("GuessingGame", options);
            System.exit(1);
        }
    }
}
