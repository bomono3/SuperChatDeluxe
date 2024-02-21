package SuperChatDeluxe.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;


import SuperChatDeluxe.model.User;

public class HttpsDAO {
	
	public HttpResponse<String> postAuthenticateUser(String username, String password){
		HttpResponse<String> response = null;
		
		
		Map<String, String> credentials = new HashMap<>();
	    credentials.put("username", username);
	    credentials.put("password", password);

	    ObjectMapper mapper = new ObjectMapper();
	    
	    try {
	    	String json = mapper.writeValueAsString(credentials);
	    	HttpClient client = HttpClient.newHttpClient();
	    	HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create("http://localhost:8080/authenticate"))
	                .header("Content-Type", "application/json")
	                .POST(HttpRequest.BodyPublishers.ofString(json))
	                .build();

	    	response = client.send(request, HttpResponse.BodyHandlers.ofString());
	    }
	    catch(IOException | InterruptedException e) {
			System.out.println("Either there was an interruption or an I/O Exception has occured");
		}
	    
		return response;
	}
	
	public HttpResponse<String> postRegisterUser(User user) {
		HttpResponse<String> response = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(user);

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/api/register"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();

			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		}
		catch(IOException | InterruptedException e) {
			System.out.println("Either there was an interruption or an I/O Exception has occured");
		}
		
		return response;
	}
	
	public void postMessageToDatabase(String username, String jwtToken,String message, boolean isPrivate, String sentTo, LocalDateTime timeSent) {
    	String jsonData;
    	
    	
    	
    	if(sentTo.equals("null")) {
    		jsonData = String.format("{\"username\": \"%s\"," +
                    "\"message\": \"%s\"," +
                    "\"isPrivate\": %s," +
                    "\"timeSent\": \"%s\"}",
                    username, message, isPrivate, timeSent);
    	}
    	else {
    		jsonData = String.format("{\"username\": \"%s\"," +
    	                                "\"message\": \"%s\"," +
    	                                "\"isPrivate\": %s," +
    	                                "\"sentTo\": \"%s\"," +
    	                                "\"timeSent\": \"%s\"}",
    	                                username, message, isPrivate, sentTo, timeSent);
    	}
    	

	   	 HttpClient client = HttpClient.newHttpClient();
	     
	   	 String url = String.format("http://localhost:8080/api/message");
	     
	     HttpRequest request = HttpRequest.newBuilder()
	             .uri(URI.create(url))
	             .header("Content-Type", "application/json")
	             .header("Authorization", "Bearer " + jwtToken)
	             .POST(BodyPublishers.ofString(jsonData))
	             .build();
	     try {
	    	 client.send(request, BodyHandlers.ofString());
	
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
    }
	
	public HttpResponse<String> getMessageBetweenDates(String username, String jwtToken ,String startDateTime, String endDateTime) throws IllegalArgumentException, MismatchedInputException {
		HttpResponse<String> response = null;

		try {
				HttpClient client = HttpClient.newHttpClient();
				String url = String.format("http://localhost:8080/api/message/gone/%s/%s/%s", username, startDateTime, endDateTime);
				HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
	            .header("Authorization", "Bearer " + jwtToken)
				.GET()
				.build();
		
		
			 response = client.send(request, HttpResponse.BodyHandlers.ofString());
		}
		
		catch(IllegalArgumentException e) {
			throw e;
		}
		catch(MismatchedInputException e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
		
		return response;

	}
	
	public HttpResponse<String> getLastMessages(String username, String jwtToken, int limit){
		HttpResponse<String> response = null;
		
		try {
			HttpClient client = HttpClient.newHttpClient();
			String url = String.format("http://localhost:8080/api/message/last/%s/%d", username, limit);
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
	            .header("Authorization", "Bearer " + jwtToken)
				.GET()
				.build();

			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return response;
		
	}
	
	
	
	
}
