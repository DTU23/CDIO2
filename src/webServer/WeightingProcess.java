package webServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Method to start the weighting procedure
 */

public class WeightingProcess {

	SocketController socketController;

	public void run() {
		try {
			socketController = new SocketController("localhost", 8000);
		} catch (IOException e) {
			System.out.println("Connection couldn't be established!");
			e.printStackTrace();
			System.exit(1);
		}
		try {
			socketController.sendCommand("RM20 8 \"Opr Nr?\" \"\" \"&3\"");
			waitResponse();
			waitResponse();
			socketController.sendCommand("P111 \"Anders And [->\"");
			waitResponse();
			waitResponse();
			socketController.sendCommand("RM20 8 \"Batch?\" \"\" \"&3\"");
			waitResponse();
			waitResponse();
			socketController.sendCommand("P111 \"Salt [->\"");
			waitResponse();
			waitResponse();
			socketController.sendCommand("P111 \"\"");
			waitResponse();
			socketController.sendCommand("T");
			waitResponse();
			socketController.sendCommand("P111 \"Placer tara [->\"");
			waitResponse();
			waitResponse();
			socketController.sendCommand("S");
			waitResponse();
			socketController.sendCommand("T");
			waitResponse();
			socketController.sendCommand("P111 \"Placer netto [->\"");
			waitResponse();
			waitResponse();
			socketController.sendCommand("S");
			waitResponse();
			socketController.sendCommand("T");
			waitResponse();
			socketController.sendCommand("P111 \"Fjern brutto [->\"");
			waitResponse();
			waitResponse();
			socketController.sendCommand("S");
			waitResponse();
			socketController.sendCommand("P111 \"Afvejning ok? [->\"");
			waitResponse();
			waitResponse();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private String waitResponse() throws IOException, InterruptedException {
		String response = socketController.nextResponse();
		while(response == null) {
			TimeUnit.MILLISECONDS.sleep(1);
			response = socketController.nextResponse();
		}
		return response;
	}
}