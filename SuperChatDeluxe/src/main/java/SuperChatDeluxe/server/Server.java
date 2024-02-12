package SuperChatDeluxe.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private static final int PORT = 5050; // Example port number
    // Creates a thread-safe list of client handlers
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println("Server starting...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                // Synchronization is not required here because add is a thread-safe operation
                // for synchronizedList
                clients.add(clientHandler);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcasts a message to all clients except the sender
    public static void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clients) { // Synchronize on the clients list to ensure thread safety during iteration
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }
}
