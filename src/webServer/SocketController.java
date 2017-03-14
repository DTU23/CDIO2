package webServer;

import java.net.*;
import java.io.*;

public class SocketController {

	private Socket clientSocket; 
	private BufferedReader input;
	private DataOutputStream output;


	public SocketController(String IP, int PORT) throws IOException {
		clientSocket = new Socket(IP, PORT);
		new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
		new DataOutputStream(clientSocket.getOutputStream());
	}


	public void sendCommand(String command) throws IOException{
		output.writeBytes(command + "\r\n");
		output.flush();
		System.out.println("Bruger sendte kommandoen: " + command);

	}

	public String nextResponse() throws IOException {
		String nextLine = input.readLine();
		if(nextLine != null) {
			System.out.println(nextLine);
		}
		return nextLine;
	}


}
