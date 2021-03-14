package NetworksProject1;

import java.io.*;
import java.net.*;


public class RobustServer {
    public static void main(String[] args) {
        ServerSocket welcomeSocket = null;
        int clientId=0;

        try {

            // create welcoming socket at port 6789
            welcomeSocket = new ServerSocket(6789);
            System.out.println("Server up and running, waiting for requests....");

            while (true) {
                //for visualization purpose, the clientId will count the clients and display which one is processed.
                // wait on welcoming socket for contact by client, creates a new socket
                Socket connectionSocket = welcomeSocket.accept();

                clientId++;//a new client is connected
                System.out.println("Client "+ clientId +" connected at: "+ connectionSocket.getInetAddress().getHostAddress());

                // create a new thread object 
                ClientHandler clientSock= new ClientHandler(connectionSocket,clientId);
                // This thread will handle the client separately
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (welcomeSocket != null) {
                try {
                    welcomeSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ClientHandler class 
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final int clientId;

        // Constructor 
        public ClientHandler(Socket socket,int clientId)
        {
            this.clientSocket = socket;
            this.clientId=clientId;
        }

        public void run()
        {
            String clientSentence,capitalizedSentence;
            DataOutputStream outToClient = null;
            BufferedReader inFromClient = null;
            System.out.println("Processing client " + clientId);

            try {
                //create input stream attached to socket
                inFromClient = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                // create output stream attached to socket
                outToClient = new DataOutputStream(clientSocket.getOutputStream());

                clientSentence = inFromClient.readLine(); // read in line from socket
                capitalizedSentence = clientSentence.toUpperCase() + '\n'; // make sure to add carriage return here!

                outToClient.writeBytes(capitalizedSentence); // write out line to socket

                System.out.println("Done with client " + clientId);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (outToClient != null) {
                        outToClient.close();
                    }
                    if (inFromClient!= null) {
                        inFromClient.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
