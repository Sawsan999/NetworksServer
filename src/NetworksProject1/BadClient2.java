package NetworksProject1;

import java.io.IOException;
import java.net.Socket;

public class BadClient2 {

    public static void main(String[] args) throws Exception {
        String sentence; // data to send to server
        String modifiedSentence; // received from server
        int id = 0;
        while (true) {
            System.out.println(++id);
            try {
                // create client socket and connect to server; this initiates TCP cnx between client and server
                Socket clientSocket = new Socket("localhost", 6789);
                Thread.sleep(500);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}