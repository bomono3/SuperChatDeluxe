package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            closeEverything(socket, out, in);
        }
    }

    @Override
    public void run() {
        String clientMessage;
        try {
            while ((clientMessage = in.readLine()) != null) {
                // For simplicity, directly calling broadcastMessage without specifying sender
                // In a real application, you might want to exclude the sender or add other
                // logic
                Server.broadcastMessage(clientMessage, this);
            }
        } catch (IOException e) {
            closeEverything(clientSocket, out, in);
            // Properly remove the client handler from the list on exception
            synchronized (Server.clients) {
                Server.clients.remove(this);
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void closeEverything(Socket socket, PrintWriter out, BufferedReader in) {
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
}
