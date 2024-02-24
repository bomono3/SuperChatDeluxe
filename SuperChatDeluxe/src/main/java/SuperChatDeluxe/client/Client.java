package SuperChatDeluxe.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import SuperChatDeluxe.model.Message;
import SuperChatDeluxe.model.User;
import SuperChatDeluxe.service.ConsoleGuiService;
import SuperChatDeluxe.service.JSwingGuiService;
import SuperChatDeluxe.util.RSA;
import SuperChatDeluxe.util.HttpsDAO;



public class Client implements JSwingGuiService.MessageCallback{
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;
	private boolean isConsoleGui = true;
	private RSA keyHolder = new RSA();
	private static HttpsDAO httpsDAO = new HttpsDAO();


	// Indicates if the client is in live mode or search mode
	private boolean live = true;
	private List<String> missedMessages = new ArrayList<>();

	private ConsoleGuiService gui;
	//lance: new swingGui
	private JSwingGuiService swingGui;
	private String jwtToken;

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
		
		keyHolder.init();
		
		File dir = new File("./" + username);
		dir.mkdir();
		File privateKeyFile = new File(dir, "privateKey.txt");
		privateKeyFile.createNewFile();
		FileWriter fileWriter = new FileWriter("./" + username + "/privateKey.txt");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.println(keyHolder.encode(keyHolder.getPrivateKey().getEncoded()));
		printWriter.close();
		fileWriter.close();
    
		User user = new User(username, password, keyHolder.encode(keyHolder.getPublicKey().getEncoded()));
    
		HttpResponse<String> response = httpsDAO.postRegisterUser(user);
		
		
		if (response != null && response.statusCode() == 200) {
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
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = httpsDAO.postAuthenticateUser(username, password);
        
		if (response != null && response.statusCode() == 201) {

			Map<String, String> responseMap = mapper.readValue(response.body(),
					new TypeReference<Map<String, String>>() {
					});


			jwtToken = responseMap.get("jwt");
			
			// Set the username field upon successful login
			this.username = username;
			File file = new File("./" + username + "/privateKey.txt");
			Scanner fileScanner = new Scanner(file);
			String privKey = fileScanner.nextLine();
			fileScanner.close();
			
			String pubKey = httpsDAO.getPublicKeyByUsername(username, jwtToken);
			keyHolder.initFromStrings(privKey, pubKey);
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
			if(isConsoleGui) {
				boolean potentialPrivateMessage = message.length() >= 8;
				if((message.split(" ").length) >= 3 && (potentialPrivateMessage)){
					
					if(message.split(" ")[1].startsWith("-private")){
						if(!message.split(" ")[2].contains(username)){
							gui.addMessage("Private message sent.", true);
						}
					}
					else{
						gui.addMessage(message, true);
					}
				}

				gui.addMessage(message, true);
				bufferedWriter.write(message);
			}
			else {
				if(message.startsWith("/exit") && swingGui.getLive()) {
					bufferedWriter.write(message);
				}
				else if(message.startsWith("/exit") && !swingGui.getLive()) {
					live = true;
					swingGui.initializeSwingChatGuiReturn("Welcome back to the chat " + username + ".", missedMessages, "Exiting search mode. You're now live.");
					swingGui.setLive(true);
					return;
				}
				else if(message.startsWith("/search")) {
						live = false;
						bufferedWriter.write(message);
						bufferedWriter.newLine();
						bufferedWriter.flush();
						swingGui.searchBetweenDates(username, jwtToken, keyHolder);
						return;
				}
				else if(message.startsWith("-private")) {
					PublicKey recipientKeyData;
					boolean potentialPrivateMessage = message.length() >= 8;
					if((message.split(" ").length) >= 3 && (potentialPrivateMessage))
					{
						String recipient = message.split(" ", 3)[1];
						String pubKey = httpsDAO.getPublicKeyByUsername(recipient, jwtToken);
						if((pubKey == null) || (pubKey == "")) {
							swingGui.addMessage("User does not exist.");
							return;
						} else {
							recipientKeyData = keyHolder.createPublicKeyFromString(pubKey);
						}
						
						String encryptedMessage = null;
						try {
							encryptedMessage = keyHolder.encrypt(message.split(" ", 3)[2], recipientKeyData);
						} catch (Exception e) {
							e.printStackTrace();
						}
						String encryptedMessageSelf = null;
						try {
							encryptedMessageSelf = keyHolder.encrypt(message.split(" ", 3)[2], keyHolder.getPublicKey());
						} catch (Exception e) {
							e.printStackTrace();
						}
						bufferedWriter.write(username + ": -private " + recipient + " " + encryptedMessage);
						bufferedWriter.newLine();
						bufferedWriter.flush();
						
						bufferedWriter.write(username + ": -private " + username + " " + encryptedMessageSelf);
						bufferedWriter.newLine();
						bufferedWriter.flush();
						return;
					}
					else {
						swingGui.addMessage("Private command incomplete. Must be in the form (-private username message)");
						return;
					}
				}
				else {
					swingGui.addMessage(username + ": " + message);
					bufferedWriter.write(username + ": " + message);
				}
				
			}

			bufferedWriter.newLine();
			bufferedWriter.flush();

		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	

	public void sendUsername(String username){
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
							if(messageFromGroupChat.split(": ", 2).length >= 2) {
								String messageWithOnlyUsername = messageFromGroupChat.split(": ", 2)[0];
								String messageWithoutUsername = messageFromGroupChat.split(": ", 2)[1];
								if(messageWithOnlyUsername.contains("(private)")) {
									String decodedMessage;
									try {
										decodedMessage = keyHolder.decrypt(messageWithoutUsername);
									} catch (Exception e) {
										decodedMessage = "Some sort of error has occured with decoding, check your private keys.";
									}
									missedMessages.add(messageWithOnlyUsername + ": " + decodedMessage);
								}
								else {
									missedMessages.add(messageFromGroupChat);
								}
							} else {
								gui.addMessage(messageFromGroupChat, false);
								
//								why add message if not live?
							}
							
						} else {
							if(messageFromGroupChat.split(": ", 2).length >= 2) {
								String messageWithOnlyUsername = messageFromGroupChat.split(": ", 2)[0];
								String messageWithoutUsername = messageFromGroupChat.split(": ", 2)[1];
								if(messageWithOnlyUsername.contains("(private)")) {
									String decodedMessage;
									try {
										decodedMessage = keyHolder.decrypt(messageWithoutUsername);
									} catch (Exception e) {
										decodedMessage = "Some sort of error has occured with decoding, check your private keys.";
									}
									
									gui.addMessage(messageWithOnlyUsername + ": " + decodedMessage, false);
									if(!isConsoleGui)
										swingGui.addMessage(messageWithOnlyUsername + ": " + decodedMessage);
								}
								else {
									gui.addMessage(messageFromGroupChat, false);
									if(!isConsoleGui)
										swingGui.addMessage(messageFromGroupChat);
								}
							}
							else
							{
								gui.addMessage(messageFromGroupChat, false);
								if(!isConsoleGui)
									swingGui.addMessage(messageFromGroupChat);
							}

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
				searchBetweenDates(scanner);

			// Exit search mode and display missed messages
			} else if ("/exit".equals(input.trim()) && (live == false)) {
				live = true;
				gui.initializeConsoleChatGuiReturn("Welcome back to the chat " + username + ".", missedMessages, "Exiting search mode. You're now live.");
				missedMessages.clear();

			// Send message to server if in live mode
			} else if ("/exit".equals(input.trim()) && (live == true)) {
				sendMessage("/exit");
				return;
			} else if(input.startsWith("-private")) {
				PublicKey recipientKeyData;
				boolean potentialPrivateMessage = input.length() >= 8;
				if((input.split(" ").length) >= 3 && (potentialPrivateMessage))
				{
					String recipient = input.split(" ", 3)[1];
					String pubKey = httpsDAO.getPublicKeyByUsername(recipient, jwtToken);
					if((pubKey == null) || (pubKey == "")) {
						gui.addMessage("User does not exist.", true);
						continue;
					} else {
						recipientKeyData = keyHolder.createPublicKeyFromString(pubKey);
					}
					
					String encryptedMessage = null;
					try {
						encryptedMessage = keyHolder.encrypt(input.split(" ", 3)[2], recipientKeyData);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String encryptedMessageSelf = null;
					try {
						encryptedMessageSelf = keyHolder.encrypt(input.split(" ", 3)[2], keyHolder.getPublicKey());
					} catch (Exception e) {
						e.printStackTrace();
					}
					sendMessage(username + ": -private " + recipient + " " + encryptedMessage);
					sendMessage(username + ": -private " + username + " " + encryptedMessageSelf);
				}
				else {
					gui.addMessage("User does not exist.", true);
				}
			}
				else if (live) {
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
			HttpResponse<String> response = httpsDAO.getMessageBetweenDates(this.username, this.jwtToken, startDateTime, endDateTime);

			ObjectMapper mapper = new ObjectMapper();


			//registering JavaTimeModule to handle LocalDateTime
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			
			List<Message> messages = mapper.readValue(response.body(), new TypeReference<List<Message>>(){});
			for(Message message : messages) {
				if(message.getIsPrivate() == true)
				{
					String messageWithOnlyUsername = message.getMessage().split(": ", 2)[0];
					String messageWithoutUsername = message.getMessage().split(": ", 2)[1];
					String decodedMessage;
					try {
						decodedMessage = keyHolder.decrypt(messageWithoutUsername);
					} catch (Exception e) {
						decodedMessage = "Some sort of error has occured with decoding, check your private keys.";
					}
					message.setMessage(messageWithOnlyUsername + ": " + decodedMessage);
				}
			}
			gui.displaySearch("Messages between " + startDate + " and " + endDate, messages);
			break;
		} 
		catch(IllegalArgumentException | MismatchedInputException e) {
			gui.addMessage("Invalid Format in Input. Must be YYYY-MM-DD", true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		}
		
	}

	//method to send a message to the server to get the message history of a user for last N messages
	public void fetchLastMessages(int limit) {
		try {
			HttpResponse<String> response = httpsDAO.getLastMessages(this.username, jwtToken, limit);

			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

			List<Message> messages = mapper.readValue(response.body(), new TypeReference<List<Message>>(){});
			
			//reverse list so that it shows as oldest -> latest
			Collections.reverse(messages);
			
			for(Message message : messages) {
				if(message.getIsPrivate() == true)
				{
					String messageWithOnlyUsername = message.getMessage().split(": ", 2)[0];
					String messageWithoutUsername = message.getMessage().split(": ", 2)[1];
					String decodedMessage;
					try {
						decodedMessage = keyHolder.decrypt(messageWithoutUsername);
					} catch (Exception e) {
						decodedMessage = "Some sort of error has occured with decoding, check your private keys.";
					}
					message.setMessage(messageWithOnlyUsername + ": " + decodedMessage);
				}
			}
			
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
