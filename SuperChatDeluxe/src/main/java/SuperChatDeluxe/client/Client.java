package SuperChatDeluxe.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import SuperChatDeluxe.model.Message;
import SuperChatDeluxe.model.User;
import SuperChatDeluxe.service.ConsoleGuiService;
import SuperChatDeluxe.service.JSwingGuiService;


public class Client implements JSwingGuiService.MessageCallback{
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;
	//lance: flag for user decision 
	private boolean isConsoleGui = true;

	// Indicates if the client is in live mode or search mode
	private boolean live = true;
	private List<String> missedMessages = new ArrayList<>();

	private ConsoleGuiService gui;
	//lance: new swingGui
	private JSwingGuiService swingGui;

	public Client(Socket socket, String username) {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.username = username;
			this.gui = new ConsoleGuiService();
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

	//method for Sign up
	private String jwtToken;

	public boolean signUp(Scanner scanner) throws IOException, InterruptedException {
		gui.addMessage("Enter username: ", true);
		String username = scanner.nextLine();
		gui.addMessage("Enter password: ", true);
		String password = scanner.nextLine();
		gui.addMessage("Confirm password: ", true);
		String confirmPassword = scanner.nextLine();

		if(username.split(" ").length > 1) {
			gui.addMessage("Signup failed: Username must not contain spaces", true);
			return false;
		}
		if (!password.equals(confirmPassword)) {
			gui.addMessage("Signup failed: Passwords do not match", true);
			return false;
		} else {
			gui.addMessage("Passwords match", true);
		}

		User user = new User(username, password);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/api/register"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() == 200) {
			this.username = username;
			gui.addMessage("Signup successful! You may login now.", true);
			return true;
		} else {
			ObjectMapper errorMapper = new ObjectMapper();
		    JsonNode jsonResponse = errorMapper.readTree(response.body());
		    String errorMessage = jsonResponse.get("message").asText();
			gui.addMessage("Signup failed: " + errorMessage, true);
			return false;
		}
	}


	//method for login
	public boolean login(Scanner scanner) throws IOException, InterruptedException {
		gui.addMessage("Enter username: ", true);
        String username = scanner.nextLine();
        gui.addMessage("Enter password: ", true);
        String password = scanner.nextLine();

        // Creating the payload using a Map
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(credentials);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/authenticate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() == 200 || response.statusCode() == 201) {

			Map<String, String> responseMap = mapper.readValue(response.body(),
					new TypeReference<Map<String, String>>() {
					});


			jwtToken = responseMap.get("jwt");
			
			// Set the username field upon successful login
			this.username = username;
			gui.addMessage("Login Successful", true);
			return true;
		} else {
			gui.addMessage("Login failed: " + response.body(), true);
			return false;
		}
    }

	public void userAuthenticate(Scanner scanner) throws IOException, InterruptedException {
		while (true) {
			gui.initializeConsoleChatGuiReturn("Welcome to SuperChatDeluxe!", 
					new ArrayList<String>() {{add("1. Sign Up"); add("2. Login"); }}, "Choose an option (1 or 2): ");
			String option = scanner.nextLine();

			if ("1".equals(option)) {
				boolean signUpSuccess = signUp(scanner);
				if(signUpSuccess && login(scanner)) {
					
					//lance: extra layer of decisions because of JSwing 
					gui.addMessage("Please enter 1 for console GUI or 2 for JSwing GUI.", true);
					option = scanner.nextLine();
					if(option.equals("2")) {
						//lance: upon successful login and user wants JSwing, start up the GUI
						isConsoleGui = false;
						swingGui = new JSwingGuiService();
						swingGui.setMessageCallback(this);
						break;
					}
					else if(option.equals("1"))
						break;
					else
						gui.addMessage("Invalid option. Please enter 1 for console GUI or 2 for JSwing GUI", true);
					
				}
			} 
			else if ("2".equals(option)) {
				boolean success = login(scanner);
				if(success) {
					
					//lance: extra layer of decisions because of JSwing 
					gui.addMessage("Please enter 1 for console GUI or 2 for JSwing GUI.", true);
					option = scanner.nextLine();
					if(option.equals("2")) {
						//lance: upon successful login and user wants JSwing, start up the GUI
						isConsoleGui = false;
						swingGui = new JSwingGuiService();
						swingGui.setMessageCallback(this);
						break;
					}
					else if(option.equals("1")){
						break;
					}
					else
						gui.addMessage("Invalid option. Please enter 1 for console GUI or 2 for JSwing GUI", true);
				}
			} 
			else {
				gui.addMessage("Invalid option. Please enter 1 for Sign Up or 2 for Login.", true);
			}
		}

		// Ensure username is not null before sending
		if (this.username != null) {
			sendUsername(this.username);
			sendJwt(jwtToken);
		} else {
			gui.addMessage("Error: Username not set.  Please try again.", true);
		}
	}

	//	send message to client handler
	public void sendMessage(String message) {
		try {
	
			//lance: !live not needed for chatroom capabilities, was WIP
			if(isConsoleGui || !live) {
				gui.addMessage(message, true);
				bufferedWriter.write(message);
			}
			else {
				swingGui.addMessage(username + ": " + message);
				bufferedWriter.write(username + ": " + message);
			}
			bufferedWriter.newLine();
			bufferedWriter.flush();

		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	

	public void sendUsername(String username) {
		try {
			bufferedWriter.write(username);
			bufferedWriter.newLine();
			bufferedWriter.flush();

		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void sendJwt(String jwt) {
		try {
			bufferedWriter.write(jwt);
			bufferedWriter.newLine();
			bufferedWriter.flush();

		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	public void listenForMessage() {
		new Thread(() -> {
			String messageFromGroupChat;
			try {
				while ((messageFromGroupChat = bufferedReader.readLine()) != null) {
					
						if (!live) {
							// In search mode, store messages instead of immediately displaying them
							missedMessages.add(messageFromGroupChat);
						} else {
							gui.addMessage(messageFromGroupChat, false);
							if(!isConsoleGui)
								swingGui.addMessage(messageFromGroupChat);
						}
					
				}
				
				closeEverything(socket, bufferedReader, bufferedWriter);
			} catch (IOException e) {
				closeEverything(socket, bufferedReader, bufferedWriter);
			}
		}).start();
	}

	//lance: added conditional checks to allow whatever is inputted into the console to also be displayed to the JSwing
	public void handleUserInput(Scanner scanner) {
		while (socket.isConnected()) {
			
			String input = scanner.nextLine();
			// Toggle live/search mode based on user commands
			if ("/search".equals(input.trim())) {
				live = false; // Enter search mode
				System.out.println("You're now in search mode.");
				sendMessage("/search");
				enterSearchMode();
				
				if(!isConsoleGui) {
					swingGui.clearChat();
					swingGui.addMessage("You are now in search mode.\n");
				}
				searchBetweenDates(scanner);

			// Exit search mode and display missed messages
			} else if ("/exit".equals(input.trim()) && (live == false)) {
				live = true;
				exitSearchMode();
				gui.initializeConsoleChatGuiReturn("Welcome back to the chat " + username + ".", missedMessages, "Exiting search mode. You're now live.");
				
				if(!isConsoleGui)
					swingGui.initializeSwingChatGuiReturn("Welcome back to the chat " + username + ".", missedMessages, "Exiting search mode. You're now live.");
				
				missedMessages.clear();

			// Send message to server if in live mode
			} else if ("/exit".equals(input.trim()) && (live == true)) {
				sendMessage("/exit");
				return;
			}	else if (live) {
				sendMessage(username + ": " + input);
			}
		}
		scanner.close();
	}
	
	//lance: WIP
	private void enterSearchMode() {
		live = false;
		swingGui.clearChat();
		swingGui.addMessage("You are now in search mode.\n");
	}
	
	//lance: WIP
	private void exitSearchMode() {
		live = true;
		swingGui.initializeSwingChatGuiReturn("Welcome back to the chat " + username + ".", missedMessages, "Exiting search mode. You're now live.");
	}
	

	// This method sends a request to the server to get messages between two dates
	//lance: added conditional checks to allow whatever is inputted into the console to also be displayed to the JSwing
	public void searchBetweenDates(Scanner scanner) {
		while(true) {
			//should be when search between dates is selected
			gui.addMessage("Enter start date (YYYY-MM-DD):", true);
			if(!isConsoleGui)
				swingGui.addMessage("Enter start date (YYYY-MM-DD):");
			
			String startDate = scanner.nextLine();
			String startDateTime = startDate + "T00:00:01";
			//should be when search is exited
			gui.addMessage("Enter end date (YYYY-MM-DD):", true);
			if(!isConsoleGui)
				swingGui.addMessage("Enter end date (YYYY-MM-DD):");
			
			String endDate = scanner.nextLine();
			String endDateTime = endDate + "T23:59:59";

			try {
				HttpClient client = HttpClient.newHttpClient();
				String url = String.format("http://localhost:8080/api/message/gone/%s/%s/%s", this.username, startDateTime, endDateTime);
				HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
		            .header("Authorization", "Bearer " + jwtToken)
					.GET()
					.build();

				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


				ObjectMapper mapper = new ObjectMapper();

				//registering JavaTimeModule to handle LocalDateTime
				mapper.registerModule(new JavaTimeModule());
				mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

				List<Message> messages = mapper.readValue(response.body(), new TypeReference<List<Message>>(){});

				gui.displaySearch("Messages between " + startDate + " and " + endDate, messages);
				if(!isConsoleGui)
					swingGui.displaySearch("Messages between " + startDate + " and " + endDate, messages);
			
				break;
			} 
			catch(IllegalArgumentException | MismatchedInputException e) {
				gui.addMessage("Invalid Format in Input. Must be YYYY-MM-DD", true);
				if(!isConsoleGui)
					swingGui.addMessage("Invalid Format in Input. Must be YYYY-MM-DD");
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	//method to send a message to the server to get the message history of a user for last N messages
	public void fetchLastMessages(int limit) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			String url = String.format("http://localhost:8080/api/message/last/%d", limit);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
		            .header("Authorization", "Bearer " + jwtToken)
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

			List<Message> messages = mapper.readValue(response.body(), new TypeReference<List<Message>>() {
			});
			
			//reverse list so that it shows as oldest -> latest
			Collections.reverse(messages);

			
			gui.initializeClear("Welcome to Gamerchat", messages, "to search between dates type /search, then type /exit to return to live chat");
			if(!isConsoleGui)
				swingGui.initializeClear("Welcome to Gamerchat", messages, "to search between dates type /search, then type /exit to return to live chat");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// Connection setup and starting the client
		try {
			Socket socket = new Socket("localhost", 5050);
			Scanner scanner = new Scanner(System.in);

			Client client = new Client(socket, null);
			client.userAuthenticate(scanner);
			client.listenForMessage();
			

			// Fetch last 10 messages
			int lastMessageLimit = 10;
			client.fetchLastMessages(lastMessageLimit);
			client.handleUserInput(scanner);
			scanner.close();
			client.gui.addMessage("Exited the Chatroom and Application. Goodbye!", false);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
