package SuperChatDeluxe.service;

import java.util.List;

import org.springframework.stereotype.Service;

import SuperChatDeluxe.model.Message;

@Service
public class ConsoleGuiService {

	String generalizedChatString = "*&*";

	String consoleTop = "****************************************************************\n"
							 + "*&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*\n"
							 + "*&************************************************************&*";

	String consoleBottom = "*&************************************************************&*\n"
								+ "*&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*\n"
								+ "****************************************************************";

	String consoleSide = "*&*                                                          *&*";

	int verticalDesignLength =(int) (consoleTop.chars().filter(ch -> ch == '\n').count() + 1);
	// calculate chat lengths dynamically based off of provided console top/bottom.
	int chatLengths =  consoleTop.length()/verticalDesignLength;

	int messageLengths = chatLengths - generalizedChatString.length()*2;

	private void fitToDisplay(String messageHolder, int lastIndex) {
		if(lastIndex >= messageHolder.length())
		{
			if(messageHolder.length() == 0)
			{
				return;
			}
			System.out.print(generalizedChatString + messageHolder.charAt(messageHolder.length() - 1));
			for( int k = 0; k < (messageLengths - 1); k++) {
				System.out.print(" ");
			}
			System.out.println(generalizedChatString);
		}
		else
		{
			System.out.print(generalizedChatString + messageHolder.substring(lastIndex, messageHolder.length()));
			for( int k = 0; k < (messageLengths - (messageHolder.length() - lastIndex)); k++) {
				System.out.print(" ");
			}
			System.out.println(generalizedChatString);
		}
	}

	public void printMessage(String message) {
		int lastIndex = 0;
		String messageHolder = message;
		for(int j = 0; j + messageLengths < messageHolder.length(); j += messageLengths) {
			System.out.println(generalizedChatString + messageHolder.substring(j, j + messageLengths) + generalizedChatString);
			lastIndex = j + messageLengths + 1;
		}
		fitToDisplay(messageHolder, lastIndex);
		System.out.println(consoleSide);
	}

	public void initializeConsoleChatGui(String enterMessage, List<Message> messages, String exitMessage)  {
		System.out.println(consoleTop);
		printMessage(enterMessage);
		System.out.println(consoleSide);
		for (Message message : messages) {

			String userMessage = message.getMessage();
			printMessage(userMessage);
		}
		printMessage(exitMessage);
		System.out.println(consoleBottom);
	}

	public void initializeConsoleChatGuiReturn(String enterMessage, List<String> messages, String exitMessage) {
		System.out.println(consoleTop);
		printMessage(enterMessage);
		System.out.println(consoleSide);
		for(int i = 0; i < messages.size(); i++)
		{
			printMessage(messages.get(i));
		}
		printMessage(exitMessage);
		System.out.println(consoleBottom);
	}

	public void addMessage(String message, boolean local)  {
		// local is for if the message is printed locally or publicly.
		// clearing previous ending style lines in console using ansi escape codes
		if(local)
		{
			System.out.print("\033[1A");
			System.out.print("\033[2K");
		}
		for(int i = 0; i < verticalDesignLength; i++)
		{
			System.out.print("\033[1A");
			System.out.print("\033[2K");
		}
		printMessage(message);
		System.out.println(consoleBottom);
	}

	public void displaySearch(String enterMessage, List<Message> results)  {
		System.out.print("\033[2J");
		initializeConsoleChatGui(enterMessage, results, enterMessage + ": press /exit to exit");
	}
	
	public void initializeClear(String enterMessage, List<Message> results, String exitMessage) {
		System.out.print("\033[2J");
		initializeConsoleChatGui(enterMessage, results, exitMessage);
	}
	
	public void serverRunningUpdateGui(String enterMessage, List<String> results, String exitMessage){
		System.out.print("\033[2J");
		initializeConsoleChatGuiReturn(enterMessage, results, exitMessage);
	}
}
