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

	// Listening for socket input
	@Override
	public void notify(SocketInMessage message) {
		// Switch-case on the message type that is recieved (from SocketController)
		switch (message.getType()) {
		case B: // Set the load
			try {
				// Parsing the message to a double - and changing it in the user interface
				notifyWeightChange(Double.parseDouble(message.getMessage()));
			} catch (Exception e) {
				// In fact we catch an exception we write a error message to the output stream
				e.printStackTrace();
				socketHandler.sendMessage(new SocketOutMessage("ES"));
			}
			break;
		case D:
			// Display a message in the primary display
			weightController.showMessagePrimaryDisplay(message.getMessage()); 
			break;
		case Q:
			// Exit the program
			System.exit(0);
			break;
		case RM204:
			//TODO Not implemented yet
			break;
		case RM208:
			// Display a message in the secondary display and wait for response
			weightController.showMessagePrimaryDisplay(message.getMessage());
			RM20awaitingResponse = true;
			break;
		case S:
			// When requested the current load is written to the output stream
			socketHandler.sendMessage(new SocketOutMessage("S S      " + new DecimalFormat("#.###").format(weightOnSlider-referenceWeight) + " kg"));
			break;
		case T:
			// Save the current load temporarily
			referenceWeight = weightOnSlider;
			// Reset the primary display
			weightController.showMessagePrimaryDisplay("0.0 kg");
			// Write the load to the output stream
			socketHandler.sendMessage(new SocketOutMessage("T S      " + new DecimalFormat("#.###").format(referenceWeight) + " kg"));
			break;
		case DW:
			// Clear primary display
			weightController.showMessagePrimaryDisplay(weightOnSlider-referenceWeight + " kg");
			break;
		case K:
			// Change the key type
			if(handleKMessage(message)) {
				// Acknowledge message written to output stream
				socketHandler.sendMessage(new SocketOutMessage("K A"));
			} else {
				// Error message written to the output stream
				socketHandler.sendMessage(new SocketOutMessage("ES"));
			}
			break;
		case P111:
			// Print message in the secondary display
			weightController.showMessageSecondaryDisplay(message.getMessage());
			break;
		default:
			// Default case - write something in the secondary display
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
			// if in KeyState 1 or 4 the button will work.
			if(keyState.equals(KeyState.K1) || keyState.equals(KeyState.K4)) {
				// we save what the weight is when the weight is tared.
				referenceWeight = weightOnSlider;
				// the weight displayed is the slider minus the reference which equals 0.0 kg.
				weightController.showMessagePrimaryDisplay(weightOnSlider - referenceWeight + " kg");
			}
			break;
		case TEXT:
			// every char that is pressed, is added to a string builder.
			userInput.append(keyPress.getCharacter());
			// the current content of the string builder is displayed in the secondary display.
			weightController.showMessageSecondaryDisplay(userInput.toString());
			break;
		case ZERO:
			// if in KeyState 1 or 4 the button will work.
			if(keyState.equals(KeyState.K1) || keyState.equals(KeyState.K4)) {
				// there reference weight is reset to zero.
				referenceWeight = 0;
				// the weight in the display is reset to zero aswell.
				weightController.showMessagePrimaryDisplay(referenceWeight + " kg");
			}
			break;
		case C:
			// c button is coded to function as a backspace when the button is pressed.
			userInput.deleteCharAt(userInput.length()-1);
			// the current content of the string builder is displayed in the secondary display.
			weightController.showMessageSecondaryDisplay(userInput.toString());
			break;
		case EXIT:
			System.exit(0);
			break;
		case SEND:
			// if an RM20 command is awaiting a response.
			if(RM20awaitingResponse) {
				// the content of the string builder will be sent out on the socket.
				socketHandler.sendMessage(new SocketOutMessage("RM20 A \"" + userInput.toString() + "\""));
				// the content of the string builder is cleared.
				userInput.setLength(0);
				// the current content of the string builder is displayed in the secondary display.
				weightController.showMessageSecondaryDisplay(userInput.toString());
				// when the RM20 command has received it's response, it will be set to false until next time it's called
				RM20awaitingResponse = false;
				// else if KeyState is 3 or 4 the appropriate acknowledgement is sent out on the socket.
			} else if (keyState.equals(KeyState.K3)) {
				socketHandler.sendMessage(new SocketOutMessage("K C 4"));
			} else if(keyState.equals(KeyState.K4)) {
				socketHandler.sendMessage(new SocketOutMessage("K B 3"));
			}
			// if KeyState is 1 or 2 nothing will happen when the "Send" button is pressed
			break;
		}
	}

	@Override
	public void notifyWeightChange(double newWeight) {
		// prints the current measurement in the primary display. Which is always what the slider is on minus what has be tared.
		weightController.showMessagePrimaryDisplay(newWeight - referenceWeight + " kg");
		// saving last notification to local variable so we can access it when needed.
		weightOnSlider = newWeight;
	}
}
