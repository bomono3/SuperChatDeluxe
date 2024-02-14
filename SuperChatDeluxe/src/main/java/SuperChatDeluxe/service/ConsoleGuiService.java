package SuperChatDeluxe.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ConsoleGuiService {
	
	String generalizedChatString = "*&*";
	
	String consoleTop = "****************************************************************\n"
							 + "*&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*\n"
							 + "*&************************************************************&*";
	
	String consoleBottom = "*&************************************************************&*\n"
								+ "*&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*\n"
								+ "****************************************************************";
	
	// calculate chat lengths dynamically based off of provided console top/bottom.
	int chatLengths = (int) (consoleTop.length()/(consoleTop.chars().filter(ch -> ch == '\n').count() + 1));
	
	int messageLengths = chatLengths - generalizedChatString.length()*2;
	
	private void fitToDisplay(String messageHolder, int lastIndex) {
		if(lastIndex >= messageHolder.length())
		{
			if(messageHolder.length() == 0)
			{
				return;
			}
			System.out.println();
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
		System.out.println("*&*                                                          *&*");
	}
	
	public void initializeConsoleChatGui(String enterMessage, List<String> messages, String exitMessage) {
		System.out.println(consoleTop);
		fitToDisplay(enterMessage, 0);
		System.out.println("*&*                                                          *&*");
		for(int i = 0; i < messages.size(); i++)
		{
			printMessage(messages.get(i));
		}
		fitToDisplay(exitMessage, 0);
		System.out.println(consoleBottom);
	}
	
	public void addMessage(String message) {
		// clearing previous ending style lines in console using ansi escape codes
		System.out.print("\033[1A");
		System.out.print("\033[2K");
		System.out.print("\033[1A");
		System.out.print("\033[2K");
		System.out.print("\033[1A");
		System.out.print("\033[2K");
		printMessage(message);
		System.out.println(consoleBottom);
	}
	
	public void displaySearch(String enterMessage, List<String> results) {
		System.out.print("\033[2J");
		initializeConsoleChatGui(enterMessage, results, enterMessage + ": press /exit to exit");
	}
}
