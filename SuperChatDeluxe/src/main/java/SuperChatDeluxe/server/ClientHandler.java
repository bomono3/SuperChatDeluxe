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
import java.net.http.HttpRequest.BodyPublishers;

import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;


import SuperChatDeluxe.util.EncryptionManager;




public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    private String username;
    private String jwt;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            this.out =  new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.username = in.readLine();
            Server.broadcastMessage("SERVER: " + username + " has entered the chat!", this);
            this.jwt = in.readLine();
        } catch (IOException e) {
            closeEverything(socket, out, in);
        }
    }

    @Override
    public void run() {
        String clientMessage;

            try {
				while ((clientMessage = in.readLine()) != null) {
						// Check for control commands and handle accordingly
				        if ("/exit".equals(clientMessage) || "/search".equals(clientMessage)) {
				            if("/exit".equals(clientMessage))  throw new IOException();
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
				
				}
            }
            catch (IOException e) {
            	synchronized (Server.clients) {
			        Server.clients.remove(this);
			    }
            	closeEverything(clientSocket, out, in);
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
    
    
    

    private void closeEverything(Socket socket, BufferedWriter out, BufferedReader in) {
    	Server.broadcastMessage("SERVER: " + username + " has left the chat!", this);
    	Server.updateGui();
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
    
    public String getJwt() {
    	return this.jwt;
    }
}
