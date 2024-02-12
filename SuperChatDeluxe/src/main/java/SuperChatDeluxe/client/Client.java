package SuperChatDeluxe.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;

	public Client(Socket socket, String username) {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.username = username;
		}
		catch(IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		try {
			if(bufferedReader != null) {
				bufferedReader.close();
			}
			if(bufferedWriter != null) {
				bufferedWriter.close();
			}
			if(socket != null) {
				socket.close();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}

	}

//	send message to client handler
	public void sendMessage(Scanner scanner) {
	try {
		bufferedWriter.write(username);
		bufferedWriter.newLine();
		bufferedWriter.flush();

		while (socket.isConnected() && scanner.hasNextLine()) {
			String messageToSend = scanner.nextLine();
			bufferedWriter.write(username + ": " + messageToSend);
			bufferedWriter.newLine();
			bufferedWriter.flush();
		}
	} catch (IOException e) {
		closeEverything(socket, bufferedReader, bufferedWriter);
	}
}

	public void listenForMessage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String messageFromGroupChat;

				while(socket.isConnected()) {
					try {
						messageFromGroupChat = bufferedReader.readLine();
						System.out.println(messageFromGroupChat);
					}
					catch(IOException e) {
						closeEverything(socket, bufferedReader, bufferedWriter);
					}
				}

			}

		}).start();
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter your username for the group chat: ");
    String username = scanner.nextLine();

    try (Socket socket = new Socket("localhost", 5050)) {
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage(scanner); // Pass the scanner to sendMessage
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (scanner != null) {
            scanner.close(); // Close the scanner here
        }
    }
}
}
