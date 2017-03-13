package webServer;

import java.io.IOException;

/**
 * Method to start the weighting procedure
 */

public class WeightingProcess implements Runnable {

	SocketController socketController;

	@Override
	public void run() {
		try {
			socketController = new SocketController("localhost", 8000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			
			// Hvilken kommando kvitterer? Skal nextResponse køres så mange gange, eller vil man få alt input ved at køre det en gang? 
			
			socketController.nextResponse(); //RM20	8	"INDTAST	NR"	""	"&3"cr	lf
			socketController.sendCommand("23");
			socketController.nextResponse(); // RM20 B
			socketController.nextResponse(); // RM20 A "23"
			socketController.sendCommand("OK");																
			socketController.nextResponse(); //RM20	8	"BATCH	NR"	""	"&3" cr lf
			socketController.nextResponse();
			socketController.sendCommand("2323"); 
			socketController.nextResponse(); // RM20 B
			socketController.nextResponse(); // RM20 A
			socketController.sendCommand("OK");
			socketController.nextResponse(); // T
			socketController.nextResponse(); // P111 "PLACER TARA"
			socketController.nextResponse(); // P111 A 
			socketController.sendCommand("OK");
			socketController.nextResponse(); // S
			socketController.nextResponse(); // S S n kg
			socketController.nextResponse(); // T
			socketController.nextResponse(); // T S n kg 
			socketController.nextResponse(); // P111 "NETTO"
			socketController.nextResponse(); // P111 A
			socketController.sendCommand("OK");
			socketController.nextResponse(); // S
			socketController.nextResponse(); // S S x kg
			socketController.nextResponse(); // T
			socketController.nextResponse(); // T S x kg 
			socketController.nextResponse(); // P111 "FJERN"
			socketController.nextResponse(); // P111 A
			socketController.nextResponse(); // S
			socketController.nextResponse(); // S S -x kg
			socketController.nextResponse(); // P111 "AFV OK"
			socketController.nextResponse(); // P111 A
			socketController.sendCommand("OK");


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	
}

/**
public class WeightingProcess implements Runnable{
    private Socket socket;
    private int batchNumber;
    private float tara;
    private float brutto;
    private float netto;


    @Override
    public void run() {
        this.socket = new Socket();
        // TODO: Implements getting usernumber later
        this.batchNumber = getResponse("Batch nr.");
        inform("Place Tara");
        awaitConfirm();
        this.tara = getWeight();
        inform("Place Netto");
        awaitConfirm();
        this.netto = getWeight();
        inform("Remove prod");
        awaitConfirm();
        this.brutto = getWeight();
        inform("ok");
    }

    public string getResponse(String message){
        socket.sendCmd("RM20 \""+message+"\"crlf");
    }
    public void inform(String message){
        socket.sendCmd("P111 \""+message+"\"crlf");
    }
    public void awaitConfirm(){
        socket.receiveResponse();
    }
    public float getWeight(){
        socket.sendCmd("S crlf");
        tmp = socket.receiveResponse();
        tmp = tmp.substring(tmp.lastIndexOf('S '));
        weight = Float.parseFloat(tmp.split(' kgs')[0]);
        socket.sendCmd("T crlf");
        return weight;
    }
}
**/