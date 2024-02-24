package SuperChatDeluxe.service;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import SuperChatDeluxe.model.Message;


public class JSwingGuiService extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private MessageCallback messageCallback;
	
	//chatroom components
	private JTextArea chatTextArea;
	private JTextField inputTextField;
	private JButton sendButton;
	private JScrollPane scrollPane;
	private boolean live = true;

	public JSwingGuiService() {
		// set up the jFrame
		this.setTitle("Super Chat Deluxe");
		//size of frame can be whatever. kept small because easier to drag around the Gui during testing
		this.setSize(600,400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(new BorderLayout(10, 20));
		
		//visual panels, for testing
		JPanel northPanel = new JPanel();
		JPanel eastPanel = new JPanel();
		JPanel westPanel = new JPanel();
		JPanel southPanel = new JPanel();
		
		//chat components
		sendButton = new JButton("Send");
		inputTextField = new JTextField();
		chatTextArea = new JTextArea();
		
		
		//enables line wrap and word wrap
		chatTextArea.setLineWrap(true);
		chatTextArea.setWrapStyleWord(true);
		chatTextArea.setEditable(false);
		scrollPane = new JScrollPane(chatTextArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//for visualization
//		northPanel.setBackground(Color.red);
//		eastPanel.setBackground(Color.green);
//		westPanel.setBackground(Color.yellow);
//		southPanel.setBackground(Color.blue);
		
		// should adjust only the width to the same ratio that the same ratio as the frame
		// when increasing/decreasing size
		inputTextField.setPreferredSize(new Dimension(475, 25));
		
		
		//listener for when the send button is clicked. 
		sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	sendMessage();
            }
        });
		
		//listener for handling /search command WIP
		//adding this listener has allowed me to press enter to send messages as well as pressing the send button
		inputTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = inputTextField.getText().trim();
				if(input.equals("/search")) {
					handleSearchCommand();
				}
				else if(input.equals("/exit")) {
					handleExitCommand();
				}
				else {
					sendMessage();
				}
			}
		});
		
		//layout of the frame components
		this.add(northPanel, BorderLayout.NORTH);
		this.add(eastPanel, BorderLayout.WEST);
		this.add(westPanel, BorderLayout.EAST);
		this.add(southPanel, BorderLayout.SOUTH);
		southPanel.add(inputTextField, BorderLayout.WEST);
		southPanel.add(sendButton, BorderLayout.EAST);
		this.add(scrollPane, BorderLayout.CENTER);
		
		
        //sets the location of the frame to the center of your screen everytime
        this.setLocationRelativeTo(null);
        this.setVisible(true);
	}
	
	//
	public void addMessage(String message) {
		chatTextArea.setText(chatTextArea.getText() + message + "\n");
	}
	
	
	public void initializeClear(String enterMesssage, List<Message> results, String exitMessage) {
		chatTextArea.setText(enterMesssage + "\n\n"); 
		for(int i = 0; i < results.size(); i++)
			chatTextArea.setText(chatTextArea.getText() + results.get(i).getMessage() + "\n");
		chatTextArea.setText(chatTextArea.getText() + "\n" + exitMessage + "\n");
	}
	
	public void initializeSwingChatGui(String enterMessage, List<Message> messages, String exitMesssage) {
		chatTextArea.setText(enterMessage + "\n\n"); 
		for(int i = 0; i < messages.size(); i++)
			chatTextArea.setText(chatTextArea.getText() + messages.get(i).getMessage() + "\n");
		chatTextArea.setText(chatTextArea.getText() + "\n" + exitMesssage);
	}
	
	public void initializeSwingChatGuiReturn(String enterMessage, List<String> messages, String exitMessage) {
		chatTextArea.setText(enterMessage + "\n\n"); 
		for(int i = 0; i < messages.size(); i++)
			chatTextArea.setText(chatTextArea.getText() + messages.get(i) + "\n");
		chatTextArea.setText(chatTextArea.getText() + "\n" + exitMessage + "\n");
	}
	
	public void displaySearch(String enterMessage, List<Message> results) {
		initializeSwingChatGui(enterMessage, results, enterMessage + ": press /exit to exit");
	}
	
	public void clearChat() {

		chatTextArea.setText("");
	}
	
	//WIP
	private void handleSearchCommand() {
		addMessage("You're now in search mode.");
		live = false;
		if(messageCallback != null) {
			messageCallback.sendMessage("/search");
		}
		inputTextField.setText("");
	}
	
	
//	this uses InputDialog to execute flow in a specific order by waiting for user input for specific prompt
	public void searchBetweenDates(String username, String jwtToken) {
	    // Prompt the user to enter the start date
	    String startDate = JOptionPane.showInputDialog(this, "Enter start date (YYYY-MM-DD):");
	    if (startDate == null) { // If user cancels the input dialog
	        return;
	    }

	    String startDateTime = startDate + "T00:00:01";

	    // Prompt the user to enter the end date
	    String endDate = JOptionPane.showInputDialog(this, "Enter end date (YYYY-MM-DD):");
	    if (endDate == null) { // If user cancels the input dialog
	        return;
	    }

	    String endDateTime = endDate + "T23:59:59";

	    try {
	        HttpClient client = HttpClient.newHttpClient();
	        String url = String.format("http://localhost:8080/api/message/gone/%s/%s/%s", username, startDateTime, endDateTime);
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(url))
	                .header("Authorization", "Bearer " + jwtToken)
	                .GET()
	                .build();

	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	        ObjectMapper mapper = new ObjectMapper();

	        // Registering JavaTimeModule to handle LocalDateTime
	        mapper.registerModule(new JavaTimeModule());
	        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	        List<Message> messages = mapper.readValue(response.body(), new TypeReference<List<Message>>(){});

	        displaySearch("Messages between " + startDate + " and " + endDate, messages);

	    } catch (IllegalArgumentException | MismatchedInputException e) {
	        addMessage("Invalid Format in Input. Must be YYYY-MM-DD. You are back in the live chat!");
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	//WIP
	private void handleExitCommand() {
		if(messageCallback != null) {
			if(live) {
				messageCallback.sendMessage("/exit");
				System.exit(0);
			}
			messageCallback.sendMessage("/exit");
		}
		inputTextField.setText("");

	}
	
	//sends a message entered by the user to the message callback for further processing
	// and then passed to the client
	private void sendMessage() {

		String message = inputTextField.getText();
		if(messageCallback != null) {
			messageCallback.sendMessage(message);
		}
		inputTextField.setText("");
	}
	
	//sets the provided messagecallback instance to be used for sending messages from the Gui
	public void setMessageCallback(MessageCallback messageCallback) {
		this.messageCallback = messageCallback;
	}
	
	//acts as a callback contract
	public interface MessageCallback{
		void sendMessage(String message);
	}
	
	public boolean getLive() {
		return live;
	}
	
	public void setLive(boolean live) {
		this.live = live;
	}
	
	
}
