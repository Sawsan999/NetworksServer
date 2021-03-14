package NetworksProject1;

import java.io.*;
import java.net.*;

public class TCPClient{

    public static void main(String[]args)throws Exception{
        String sentence; // data to send to server
        String modifiedSentence; // received from server

        System.out.println("Client is running, enter some text:");

        // create input stream, client reads line from standard input
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        // create client socket and connect to server; this initiates TCP cnx between client and server
        Socket clientSocket = new Socket("localhost", 6789);

        // create output stream attached to socket
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        // create input stream attached to socket
        BufferedReader inFromServer = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));

        sentence = inFromUser.readLine(); // read line from user
        outToServer.writeBytes(sentence + '\n'); //send line to server

        /*
        characters continue to accumulate in modifiedSentence until the line ends with a carriage return
         */
        modifiedSentence = inFromServer.readLine(); // read line from server

        System.out.println("FROM SERVER: " + modifiedSentence);

        clientSocket.close(); // close TCP cnx between client and server

        System.out.println("Client closed the socket and is done execution");

    }
}