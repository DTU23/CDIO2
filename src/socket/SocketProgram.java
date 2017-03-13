package socket;

import java.net.*;
import java.io.*;
import java.io.*;
public class SocketProgram {


	public void connectToServer(String IP, int PORT) throws IOException {
		Socket clientSocket = new Socket(IP, PORT);
	}

	public String sendCMD(String cmd){
		BufferedReader inFromUser =
				new BufferedReader(new InputStreamReader(System.in)); 
		DataOutputStream outToServer =
				new DataOutputStream(clientSocket.getOutputStream());
		cmd = inFromUser.readLine();
		outToServer.writeBytes(cmd + '\n');
	}

	public notify(Ctrl, ctrl)
}
