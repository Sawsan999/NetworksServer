package NetworksProject1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


public class RobustServer {

    public static final int TIMEOUT = 30;

    public static void main(String[] args) {
        int clientId = 0;
        // create welcoming socket at port 6789
        try (ServerSocket welcomeSocket = new ServerSocket(6789);) {
            System.out.println("Server up and running, waiting for requests....");
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
            while (true) {
                // for visualization purpose, the clientId will count the clients and display which one is processed.
                // wait on welcoming socket for contact by client, creates a new socket
                Socket connectionSocket = welcomeSocket.accept();
                // Add timeout to socket
                clientId++;//a new client is connected
                System.out.println("Client " + clientId + " connected at: " + connectionSocket.getInetAddress().getHostAddress());

                // create and submit new runnable to executor, if thread pool limit is already reached then the current client will wait in a queue
                ClientHandler clientSock = new ClientHandler(connectionSocket, clientId);
                executor.submit(clientSock);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ClientHandler class 
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final int clientId;

        // Constructor 
        public ClientHandler(Socket socket, int clientId) {
            this.clientSocket = socket;
            this.clientId = clientId;
        }

        public void run() {
            // Execute server code in a callable to take advantage of the future .get() with timeout
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(new Callable<String>() {
                @Override
                public String call() {
                    String clientSentence, capitalizedSentence;
                    System.out.println("Processing client " + clientId);

                    //create input and output streams attached to socket
                    try (DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
                         BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                        clientSentence = inFromClient.readLine(); // read in line from socket
                        capitalizedSentence = clientSentence.toUpperCase() + '\n'; // make sure to add carriage return here!

                        outToClient.writeBytes(capitalizedSentence); // write out line to socket

                        return "Served client " + clientId;

                    } catch (IOException e) {
                        return "Failed to serve client " + clientId;
                    }
                }
            });

            try {
                // Wait for the server code above to execute with a timeout
                future.get(TIMEOUT, TimeUnit.SECONDS);
                System.out.println(future.get());
            } catch (Exception e) {
                future.cancel(true);
                System.out.println("Connection timed out!");
            }
            executor.shutdownNow();
        }
    }
}
