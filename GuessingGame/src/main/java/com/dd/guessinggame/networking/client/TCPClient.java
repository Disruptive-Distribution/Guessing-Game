package com.dd.guessinggame.networking.client;

import com.dd.guessinggame.networking.utils.Handler;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JTextArea;

/**
 * Guessing Game Client
 *
 * @author d471061c
 */
public class TCPClient {

    private Socket socket;
    private Handler handler;
    private Listener listener;

    /**
     * Connect to the server
     *
     * @param ipAddress IP-address of the server
     * @param port Port number of the server
     * @return true if the connection was successful, false if not
     * @throws IOException If there was problems in connecting the server
     */
    public void connect(String ipAddress, int port) throws IOException {
        this.socket = new Socket(ipAddress, port);
        this.handler = new Handler(this.socket);
    }

    /**
     * Close the client
     *
     * @throws IOException If the client is not initialized
     */
    public void close() throws IOException {
        this.socket.close();
    }

    /**
     * Start thread that writes the inputs from the server to the text area in
     * the GUI. The thread also handles the states of the client if found
     * necessary.
     *
     * @throws IOException
     */
    public void start(JTextArea textArea) throws IOException {
        this.listener = new Listener(this.handler, textArea);
        this.listener.start();
    }

    /**
     * Send message to the server
     *
     * @param message Message to be sent
     */
    public void sendMessage(String message) {
        this.handler.send(message);
    }

    /**
     * *
     * This loop is run when the connection between server is established
     */
    public void mainloop() {
        Scanner scan = new Scanner(System.in);
        String message, output;

        // Handle the communication
        System.out.println("[*] Connected to the server");
        while (true) {
            message = scan.nextLine();
            if (message.equals("quit")) {
                break;
            }

            try {
                handler.send(message);
                output = handler.readLine();
                System.out.println("From server: '" + output + "'");
                //not in use rn, meant for cheacking if ready
                if (output.equals("starting")) {
                    handler.send("ready");
                }
            } catch (IOException ex) {
                System.err.println("Error" + ex.getMessage());
            }
        }

        try {
            this.socket.close();
        } catch (IOException ex) {
            System.err.println("Failed to shutdown the client: " + ex.getMessage());
        }

        System.out.println("Bye!");
    }
}
