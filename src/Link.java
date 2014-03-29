/**
 * Created by marvinbernal on 2014-03-27.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Provides a link between nodes.
 *
 * This includes socket creation and packet transmission.
 *
 * The source node is assumed to be the SERVER while the destination node is assumed to be the CLIENT node.
 */
public class Link extends Thread {


    public static final int     DEFAULT_PORT = 6066;        // Default listening port
    public static final int     SOCKET_TIMEOUT = 120000;    // Timeout in milliseconds

    public static final int     DEFAULT_COST = 1; // Default cost for link

    private Node                mSourceNode;
    private Node                mDestinationNode;
    private int                 mPort;
    private int                 mCost;

    private ServerSocket        mServerListeningSocket;  // Socket to listen for incoming connections
    private Socket              mTransmissionSocket;

    public Link(Node sourceNode, Node destinationNode){
        this(sourceNode, destinationNode, DEFAULT_PORT, DEFAULT_COST);
    }
    
    public Link(Node sourceNode, Node destinationNode, int port, int cost) {
        mSourceNode = sourceNode;
        mDestinationNode = destinationNode;
        mPort = port;
        mCost = cost;
    }

    public void setCost(int mCost) {
        this.mCost = mCost;
    }
    public void setPort(int mPort) {
        this.mPort = mPort;
    }
    public void setDestinationNode(Node mDestinationNode) {
        this.mDestinationNode = mDestinationNode;
    }
    public void setSourceNode(Node mSourceNode) {
        this.mSourceNode = mSourceNode;
    }

    public Node getSourceNode() {
        return mSourceNode;
    }
    public Node getDestinationNode() {
        return mDestinationNode;
    }
    public int getPort() {
        return mPort;
    }
    public int getCost() {
        return mCost;
    }


    public void run() {

        while(true){
            try{
                initializeConnection();
                String stringToTransmit = listenForString();

                System.out.println("MESSAGE RECEIVED: " + stringToTransmit);
                transmitAString(stringToTransmit);
            }catch (IOException e){
                e.printStackTrace();
                break;
            }
        }
    }

    private void initializeConnection(){
        try{
            mServerListeningSocket = new ServerSocket(mPort);
            mServerListeningSocket.setSoTimeout(SOCKET_TIMEOUT);

            System.out.println("Router " + getSourceNode().getRouterID() + " has connected.");

            System.out.println("Listening on port " + mServerListeningSocket.getLocalPort());
            mTransmissionSocket =  mServerListeningSocket.accept(); // blocks until a connection is made

            System.out.println("Connected to " + mTransmissionSocket.getRemoteSocketAddress());
        }catch(SocketTimeoutException socketTimeoutException){

            System.out.println("Socket timed out!");

        }catch(IOException iOException){
            iOException.printStackTrace();
        }
    }

    public void transmitAString(String messageToSend) throws IOException {
        OutputStream outputStream = mTransmissionSocket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(messageToSend);
    }

    public String listenForString() throws IOException {
        InputStream inputStream = mTransmissionSocket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        return dataInputStream.readUTF();
    }
}