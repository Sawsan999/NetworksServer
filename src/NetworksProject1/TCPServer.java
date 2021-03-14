package NetworksProject1;

//import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.*;

public class TCPServer {

    public static void main(String[] args) throws Exception {

        System.out.println("Server up and running, waiting for requests....");
        String clientSentence; // text from client
        String capitalizedSentence; // text to send to client

        // create welcoming socket at port 6789
        ServerSocket welcomeSocket = new ServerSocket(6789);

        int clientId = 0;

        while(true){
            // wait on welcoming socket for contact by client, creates a new socket
            Socket connectionSocket = welcomeSocket.accept();

            //create input stream attached to socket
            BufferedReader inFromClient = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));

            // create output stream attached to socket
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            clientId++;
            System.out.println("Processing client " + clientId);

            clientSentence = inFromClient.readLine(); // read in line from socket
            capitalizedSentence = clientSentence.toUpperCase() + '\n'; // make sure to add carriage return here!

            outToClient.writeBytes(capitalizedSentence); // write out line to socket
            System.out.println("Done with client " + clientId);

        } // loop back and wait for another client connection

    }
}