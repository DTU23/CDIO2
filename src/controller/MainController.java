package controller;

import socket.ISocketController;
import socket.ISocketObserver;
import socket.SocketInMessage;
import socket.SocketOutMessage;
import weight.IWeightInterfaceController;
import weight.IWeightInterfaceObserver;
import weight.KeyPress;
/**
 * MainController - integrating input from socket and ui. Implements ISocketObserver and IUIObserver to handle this.
 * @author Christian Budtz
 * @version 0.1 2017-01-24
 *
 */
public class MainController implements IMainController, ISocketObserver, IWeightInterfaceObserver {

	private ISocketController socketHandler;
	private IWeightInterfaceController weightController;
	private KeyState keyState = KeyState.K1;
	
	//input values
	private double referenceWeight = 0;
	private double weightInDisplay = 0;
	private StringBuilder userInput = new StringBuilder();
	

	public MainController(ISocketController socketHandler, IWeightInterfaceController uiController) {
		this.init(socketHandler, uiController);
	}

	@Override
	public void init(ISocketController socketHandler, IWeightInterfaceController uiController) {
		this.socketHandler = socketHandler;
		this.weightController = uiController;
	}

	@Override
	public void start() {
		if (socketHandler!=null && weightController!=null){
			//Makes this controller interested in messages from the socket
			socketHandler.registerObserver(this);
			//Starts socketHandler in own thread
			new Thread(socketHandler).start();
			//Makes this controller interested in messages from the weight
			weightController.registerObserver(this);
			//Starts weightController in own thread
			new Thread(weightController).start();

		} else {
			System.err.println("No controllers injected!");
		}
	}

	//Listening for socket input
	@Override
	public void notify(SocketInMessage message) {
		switch (message.getType()) {
		case B:
			break;
		case D:
			weightController.showMessagePrimaryDisplay(message.getMessage()); 
			break;
		case Q:
			break;
		case RM204:
			break;
		case RM208:
			break;
		case S:
			break;
		case T:
			break;
		case DW:
			break;
		case K:
			handleKMessage(message);
			break;
		case P111:
			break;
		}

	}

	private void handleKMessage(SocketInMessage message) {
		switch (message.getMessage()) {
		case "1" :
			this.keyState = KeyState.K1;
			break;
		case "2" :
			this.keyState = KeyState.K2;
			break;
		case "3" :
			this.keyState = KeyState.K3;
			break;
		case "4" :
			this.keyState = KeyState.K4;
			break;
		default:
			socketHandler.sendMessage(new SocketOutMessage("ES"));
			break;
		}
	}
	//Listening for UI input
	@Override
	public void notifyKeyPress(KeyPress keyPress) {
		switch (keyPress.getType()) {
		case SOFTBUTTON:
			break;
		case TARA:
			referenceWeight = referenceWeight + weightInDisplay;
			weightController.showMessagePrimaryDisplay("0.0 kg");
			break;
		case TEXT:
			userInput.append(keyPress.getCharacter());
			weightController.showMessageSecondaryDisplay(userInput.toString());
			break;
		case ZERO:
			referenceWeight = 0;
			weightController.showMessagePrimaryDisplay(referenceWeight + " kg");
			break;
		case C:
			// c button is coded to function as a backspace, but the button isn't implemented in the GUI at the moment
			userInput.deleteCharAt(userInput.length()-1);
			weightController.showMessageSecondaryDisplay(userInput.toString());
			break;
		case EXIT:
			System.exit(0);
			break;
		case SEND:
			// TODO only case not done, what is this KeyState?
			if (keyState.equals(KeyState.K4) || keyState.equals(KeyState.K3) ){
				socketHandler.sendMessage(new SocketOutMessage("K A 3"));
			}
			break;
		}
	}

	@Override
	public void notifyWeightChange(double newWeight) {
		weightController.showMessagePrimaryDisplay(newWeight - referenceWeight + " kg");
		weightInDisplay = newWeight;
	}
}
