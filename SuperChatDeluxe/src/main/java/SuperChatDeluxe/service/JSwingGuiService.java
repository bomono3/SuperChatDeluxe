package SuperChatDeluxe.service;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import SuperChatDeluxe.model.Message;


public class JSwingGuiService extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private MessageCallback messageCallback;
	
	//chatroom components
	private JTextArea chatTextArea;
	private JTextField inputTextField;
	private JButton sendButton;
	private JScrollPane scrollPane;

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
		if(messageCallback != null) {
			messageCallback.sendMessage("/search");
		}
		inputTextField.setText("");
	}
	
	//WIP
	private void handleExitCommand() {
		if(messageCallback != null) {
			messageCallback.sendMessage("/exit");
		}
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
	
	
}
