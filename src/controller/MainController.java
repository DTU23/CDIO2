package controller;

import java.text.DecimalFormat;

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
	private boolean RM20awaitingResponse = false;

	//input values
	private double referenceWeight = 0;
	private double weightOnSlider = 0;
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
			try {
				notifyWeightChange(Double.parseDouble(message.getMessage()));
			} catch (Exception e) {
				e.printStackTrace();
				socketHandler.sendMessage(new SocketOutMessage("ES"));
			}
			break;
		case D:
			weightController.showMessagePrimaryDisplay(message.getMessage()); 
			break;
		case Q:
			System.exit(0);
			break;
		case RM204:
			//TODO Not implemented yet
			break;
		case RM208:
			weightController.showMessagePrimaryDisplay(message.getMessage());
			RM20awaitingResponse = true;
			break;
		case S:
			socketHandler.sendMessage(new SocketOutMessage("S S      " + new DecimalFormat("#.###").format(weightOnSlider-referenceWeight) + " kg"));
			break;
		case T:
			referenceWeight = weightOnSlider;
			weightController.showMessagePrimaryDisplay("0.0 kg");
			socketHandler.sendMessage(new SocketOutMessage("T S      " + new DecimalFormat("#.###").format(referenceWeight) + " kg"));
			break;
		case DW:
			weightController.showMessagePrimaryDisplay(referenceWeight + " kg");
			break;
		case K:
			if(handleKMessage(message)) {
				socketHandler.sendMessage(new SocketOutMessage("K A"));
			} else {
				socketHandler.sendMessage(new SocketOutMessage("ES"));
			}
			break;
		case P111:
			weightController.showMessageSecondaryDisplay(message.getMessage());
			break;
		default:
			weightController.showMessageSecondaryDisplay(message.getMessage()); 
			break;
		}

	}

	private boolean handleKMessage(SocketInMessage message) {
		switch (message.getMessage()) {
		case "1" :
			this.keyState = KeyState.K1;
			return true;
		case "2" :
			this.keyState = KeyState.K2;
			return true;
		case "3" :
			this.keyState = KeyState.K3;
			return true;
		case "4" :
			this.keyState = KeyState.K4;
			return true;
		default:
			return false;
		}
	}
	//Listening for UI input
	@Override
	public void notifyKeyPress(KeyPress keyPress) {
		switch (keyPress.getType()) {
		case SOFTBUTTON:
			break;
		case TARA:
			// if in KeyState 1 or 4 the button will work
			if(keyState.equals(KeyState.K1) || keyState.equals(KeyState.K4)) {
				referenceWeight = weightOnSlider;
				weightController.showMessagePrimaryDisplay("0.0 kg");
			}
			break;
		case TEXT:
			userInput.append(keyPress.getCharacter());
			weightController.showMessageSecondaryDisplay(userInput.toString());
			break;
		case ZERO:
			// if in KeyState 1 or 4 the button will work
			if(keyState.equals(KeyState.K1) || keyState.equals(KeyState.K4)) {
				referenceWeight = 0;
				weightController.showMessagePrimaryDisplay(referenceWeight + " kg");
			}
			break;
		case C:
			// c button is coded to function as a backspace
			userInput.deleteCharAt(userInput.length()-1);
			weightController.showMessageSecondaryDisplay(userInput.toString());
			break;
		case EXIT:
			System.exit(0);
			break;
		case SEND:
			if(RM20awaitingResponse) {
				socketHandler.sendMessage(new SocketOutMessage("RM20 A \"" + userInput.toString() + "\""));
				userInput.setLength(0);
				weightController.showMessageSecondaryDisplay(userInput.toString());
				RM20awaitingResponse = false;
			} else if (keyState.equals(KeyState.K3)) {
				socketHandler.sendMessage(new SocketOutMessage("K A 3"));
			} else if(keyState.equals(KeyState.K4)) {
				socketHandler.sendMessage(new SocketOutMessage("K A 4"));
			}
			break;
		}
	}

	@Override
	public void notifyWeightChange(double newWeight) {
		weightController.showMessagePrimaryDisplay(newWeight - referenceWeight + " kg");
		weightOnSlider = newWeight;
	}
}
