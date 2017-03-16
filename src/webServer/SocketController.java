package webServer;

import java.net.*;
import java.io.*;

public class SocketController {

	private Socket clientSocket; 
	private BufferedReader input;
	private DataOutputStream output;

	// Makes a clientsocket and a BufferedReader and DataOutputStream
	
	public SocketController(String IP, int PORT) throws IOException {
		clientSocket = new Socket(IP, PORT);
		input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
		output = new DataOutputStream(clientSocket.getOutputStream());
	}

	// Sends a command to the simulator
	public void sendCommand(String command) throws IOException{
		output.writeBytes(command + "\r\n");
		output.flush();
		System.out.println("Bruger sendte kommandoen: " + command);

	}

	// Reads the response from the simulator
	public String nextResponse() throws IOException {
		String nextLine = input.readLine();
		if(nextLine != null) {
			System.out.println(nextLine);
		}
		return nextLine;
	}
}
