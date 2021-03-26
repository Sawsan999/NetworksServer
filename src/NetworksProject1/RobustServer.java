package NetworksProject1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class RobustServer {

    public static final int TIMEOUT = 30;

    public static void main(String[] args) {
        int clientId = 0;
        // create welcoming socket at port 6789
        try (ServerSocket welcomeSocket = new ServerSocket(6789);) {
            System.out.println("Server up and running, waiting for requests....");
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
            TreeMap<String, Integer> tmap = new TreeMap<String, Integer>();
            int maxCon=10;
            PeriodicTimer(tmap);

            while (true) {
                // for visualization purpose, the clientId will count the clients and display which one is processed.
                // wait on welcoming socket for contact by client, creates a new socket
                Socket connectionSocket = welcomeSocket.accept();
                // Add timeout to socket
                clientId++;//a new client is connected
                System.out.println("Client " + clientId + " connected at: " + connectionSocket.getInetAddress().getHostAddress());

                if(BlockedIP(maxCon, tmap,connectionSocket.getInetAddress().getHostAddress())){
                    System.out.println("Connection blocked from "+connectionSocket.getInetAddress().getHostAddress());
                }
                else {
                    // create and submit new runnable to executor, if thread pool limit is already reached then the current client will wait in a queue
                    ClientHandler clientSock = new ClientHandler(connectionSocket, clientId);
                    executor.submit(clientSock);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean BlockedIP(int maxCon, TreeMap<String, Integer> tmap, String hostAddress) {
        boolean flag=false;
        if (!tmap.containsKey(hostAddress)){
            tmap.put(hostAddress,1);
        }
        else{
            int count=tmap.get(hostAddress)+1;
            tmap.put(hostAddress, count);
            if(count>=maxCon){flag=true;}
        }
        System.out.println(tmap);
        return flag;
    }

    public static void PeriodicTimer(TreeMap<String, Integer> tmap) {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                tmap.clear();
                System.out.println("Array updated");
                System.out.println(tmap);
            }
        };
        Timer timer = new Timer("Timer");

        long delay = 0;
        long period = 1000*60;
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
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
