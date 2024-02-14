package SuperChatDeluxe.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import SuperChatDeluxe.model.Message;


public class Client {
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;

	// Indicates if the client is in live mode or search mode
	private boolean live = true;
	private List<String> missedMessages = new ArrayList<>();

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
			if(bufferedReader != null) bufferedReader.close();
			if(bufferedWriter != null) bufferedWriter.close();
			if(socket != null) socket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	//	send message to client handler
	public void sendMessage(String message) {
		try {
			bufferedWriter.write(message);
			bufferedWriter.newLine();
			bufferedWriter.flush();
		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	public void listenForMessage() {
		new Thread(() -> {
			String messageFromGroupChat;
			while (socket.isConnected()) {
				try {
					messageFromGroupChat = bufferedReader.readLine();
					if (!live) {
						// In search mode, store messages instead of immediately displaying them
						missedMessages.add(messageFromGroupChat);
					} else {
						System.out.println(messageFromGroupChat);
					}
				} catch (IOException e) {
					closeEverything(socket, bufferedReader, bufferedWriter);
					break;
				}
			}
		}).start();
	}

	public void handleUserInput() {
		Scanner scanner = new Scanner(System.in);
		while (socket.isConnected()) {
			String input = scanner.nextLine();

			// Toggle live/search mode based on user commands
			if ("/search".equals(input.trim())) {
				live = false; // Enter search mode
				System.out.println("You're now in search mode. Type /exit to return to live chat.");
				searchBetweenDates();

			// Exit search mode and display missed messages
			} else if ("/exit".equals(input.trim())) {
				live = true;
				missedMessages.forEach(System.out::println);
				missedMessages.clear();
				System.out.println("Exiting search mode. You're now live.");

			// Send message to server if in live mode
			} else if (live) {
				sendMessage(username + ": " + input);
			}
		}
		scanner.close();
	}

	// This method sends a request to the server to get messages between two dates
	public void searchBetweenDates() {
    Scanner scanner = new Scanner(System.in);
	//should be when search between dates is selected
    System.out.println("Enter start date (YYYY-MM-DD):");
    String startDate = scanner.nextLine();
	String startDateTime = startDate + "T00:00:01";
	//should be when search is exited
    System.out.println("Enter end date (YYYY-MM-DD):");
    String endDate = scanner.nextLine();
	String endDateTime = endDate + "T23:59:59";

    try {
        HttpClient client = HttpClient.newHttpClient();
        String url = String.format("http://localhost:8080/api/message/gone/%s/%s/%s", this.username, startDateTime, endDateTime);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


		ObjectMapper mapper = new ObjectMapper();

		//registering JavaTimeModule to handle LocalDateTime
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		List<Message> messages = mapper.readValue(response.body(), new TypeReference<List<Message>>(){});

        System.out.println("Messages between " + startDate + " and " + endDate + ":");

		for (Message message : messages) {
			System.out.println(" message: " + message.getMessage());
		}
    } catch (Exception e) {
        e.printStackTrace();
    }
}

	public static void main(String[] args) {
		// Connection setup and starting the client
		try {
			Socket socket = new Socket("localhost", 5050);
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter your username: ");
			String username = scanner.nextLine();
			Client client = new Client(socket, username);

			client.listenForMessage();
			client.handleUserInput();

			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
