package server;

public class StartWeigting implements Runnable{
    private Socket socket;
    private float tara;
    private int batchNumber;
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
        weight = getWeightAsFloat();
        socket.sendCmd("T crlf");
        return weight;
    }
}