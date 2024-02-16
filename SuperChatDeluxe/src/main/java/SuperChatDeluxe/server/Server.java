package SuperChatDeluxe.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import SuperChatDeluxe.service.ConsoleGuiService;

public class Server {
    private static final int PORT = 5050; // Example port number
    private static String IP;
    // Creates a thread-safe list of client handlers
    static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<ClientHandler>());
    public static ConsoleGuiService gui = new ConsoleGuiService();
    public static List<String> guiList = new ArrayList<String>() {
					private static final long serialVersionUID = 1L;

					{add("IP Address: " + IP); add("Port: " + PORT); add("NumberOfConnectedClients: " + clients.size());}};
    public static void main(String[] args) {
    	ConsoleGuiService gui = new ConsoleGuiService();
    	try {
			IP = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			IP = null;
		}
    	guiList.set(0, "IP Address: " + IP);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            
            while (true) {
            	guiList.set(2, "NumberOfConnectedClients: " + clients.size());
            	Server.gui.serverRunningUpdateGui("SERVER RUNNING", guiList, "Ctrl + C to shutdown server");
                Socket clientSocket = serverSocket.accept();
                

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
    
    public static void updateGui()  {
    	guiList.set(2, "NumberOfConnectedClients: " + clients.size());
    	gui.serverRunningUpdateGui("SERVER RUNNING", guiList, "Ctrl + C to shutdown server");
    }

    // Broadcasts a message to all clients except the sender
    public static void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clients) { // Synchronize on the clients list to ensure thread safety during iteration
        	for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        	
        	if(!message.contains("SERVER: " + sender.getUsername())) {
        		sender.postMessageToDatabase(message, false, "null", LocalDateTime.now());
        	}
        	
        }
    }
    
    public static void privateMessage(String message, String username, ClientHandler sender) {
    	synchronized (clients) { // Synchronize on the clients list to ensure thread safety during iteration
    		for (ClientHandler client : clients) {
                if (client != sender && client.getUsername().equals(username)) {
                    client.sendMessage(message);
                    sender.postMessageToDatabase(message, true, client.getUsername(), LocalDateTime.now());
                    return;
                }
            }
    		
    		sender.sendMessage("User with username " + username + " does not exist.");
    		
        }
    }
    
}
