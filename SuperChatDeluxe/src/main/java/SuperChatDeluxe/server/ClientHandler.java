package SuperChatDeluxe.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.RequestEntity.BodyBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import SuperChatDeluxe.model.Message;
import SuperChatDeluxe.model.User;
import SuperChatDeluxe.util.EncryptionManager;


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            this.out =  new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.username = in.readLine();
            Server.broadcastMessage("SERVER: " + username + " has entered the chat!", this);
        } catch (IOException e) {
            closeEverything(socket, out, in);
        }
    }

    @Override
    public void run() {
        String clientMessage;

            while (clientSocket.isConnected()) {

            	try {
					clientMessage = in.readLine();
					if(clientMessage == null) throw new IOException();

                    // Check for control commands and handle accordingly
                    if ("/exit".equals(clientMessage) || "/search".equals(clientMessage)) {
                        // Do not broadcast these commands
                        continue;
                    }


					String messageWithoutUsername = clientMessage.split(": ", 2)[1];
					boolean potentialPrivateMessage = messageWithoutUsername.length() >= 8;

					if(potentialPrivateMessage && messageWithoutUsername.substring(0,8).contains("-private")) {
            		String[] messageComponents = messageWithoutUsername.split(" ");

            			if(messageComponents.length < 3 ) {
            			sendMessage("Private command incomplete. Must contain (-private, username, message)");
            			}
            			else {
            			String[] newMessageComponent = messageWithoutUsername.split(" ", 3);
            			String newMessage = username + "(private): " + newMessageComponent[2];
            			Server.privateMessage(newMessage, messageComponents[1], this);
            			}
					}
					else {
            		Server.broadcastMessage(clientMessage, this);
					}
				} catch (IOException e) {
		            synchronized (Server.clients) {
	                Server.clients.remove(this);
	            }
					closeEverything(clientSocket, out, in);
					break;
				}




            }
    }

    public void sendMessage(String message) {
    	try {
			out.write(message);
			out.newLine();
			out.flush();
			
		} catch (IOException e) {
			closeEverything(clientSocket, out, in);
		}

    }
    
    public void postMessageToDatabase(String message, boolean isPrivate, String sentTo, LocalDateTime timeSent) {
    	String encryptedMessage = message;
    	String jsonData;
    	
    	EncryptionManager manager = new EncryptionManager();
		manager.initFromStrings();
		
		try {
			encryptedMessage = manager.encrypt(message);
			
		}
		catch(Exception ignored) {}
    	
    	
    	if(sentTo.equals("null")) {
    		jsonData = String.format("{\"username\": \"%s\"," +
                    "\"message\": \"%s\"," +
                    "\"isPrivate\": %s," +
                    "\"timeSent\": \"%s\"}",
                    this.username, encryptedMessage, isPrivate, timeSent);
    	}
    	else {
    		jsonData = String.format("{\"username\": \"%s\"," +
    	                                "\"message\": \"%s\"," +
    	                                "\"isPrivate\": %s," +
    	                                "\"sentTo\": \"%s\"," +
    	                                "\"timeSent\": \"%s\"}",
    	                                this.username, encryptedMessage, isPrivate, sentTo, timeSent);
    	}
    	
    	

    	 System.out.println("jsonData: " + jsonData);
	   	 HttpClient client = HttpClient.newHttpClient();
	     
	   	 String url = String.format("http://localhost:8080/api/message");
	     
	     HttpRequest request = HttpRequest.newBuilder()
	             .uri(URI.create(url))
	             .header("Content-Type", "application/json")
	             .POST(BodyPublishers.ofString(jsonData))
	             .build();
	     try {
	    	 client.send(request, BodyHandlers.ofString());
	
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
    }
    

    private void closeEverything(Socket socket, BufferedWriter out, BufferedReader in) {
    	Server.broadcastMessage("SERVER: " + username + " has left the chat!", this);
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
    	return this.username;
    }
}
