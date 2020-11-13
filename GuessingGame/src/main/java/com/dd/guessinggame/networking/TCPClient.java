package com.dd.guessinggame.networking;

import com.dd.guessinggame.networking.utils.Handler;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author d471061c
 */
public class TCPClient {
    
    private Socket socket;
    private Handler handler;

    /**
     * Connect to the server
     * @param ipAddress IP-address of the server
     * @param port Port number of the server
     * @return true if the connection was successful, false if not
     * @throws IOException If there was problems in connecting the server
     */
    public boolean connect(String ipAddress, int port) throws IOException {
        this.socket = new Socket(ipAddress, port);
        this.handler = new Handler(this.socket);
        return true;
    }
 
    /***
     * This loop is run when the connection between server is established
     */
    public void mainloop() {
        Scanner scan = new Scanner(System.in);
        String message, output;
        
        // Handle the communication
        while (true) {
            message = scan.nextLine();
            
            if (message.equals("quit")) {
                break;
            }
            
            try {
                handler.send(message);
                output = handler.readLine();
                System.out.println("From server: '" + output + "'");
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
