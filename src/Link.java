/**
 * Created by marvinbernal on 2014-03-27.
 */

import java.io.*;
import java.net.Socket;

/**
 * Provides a link between nodes.
 *
 * This includes socket creation and packet transmission.
 *
 * The source node is assumed to be the SERVER while the destination node is assumed to be the CLIENT node.
 */
public class Link extends Thread{


    public static final int     DEFAULT_PORT = 6066;        // Default listening port
    public static final int     SOCKET_TIMEOUT = 120000;    // Timeout in milliseconds

    public static final int     DEFAULT_COST = 1; // Default cost for link

    private Node                mSourceNode;
    private Node                mDestinationNode;
    private int                 mConnectionPort;
    private int                 mCost;
    private static int          mLinkID = 0;

    private Socket              mSocket;                     // Socket to connect to server
    private String              mMessageToSend;

    public Link(){
        // default constructor used simply for reference purposes. Note this does not increase the LinkID variable.

    }

    public Link(Node sourceNode, Node destinationNode, int connectionPort,  int cost) {
        mSourceNode = sourceNode;
        mDestinationNode = destinationNode;
        mListeningPort = listeningPort;
        mConnectionPort = connectionPort;
        mCost = cost;
        mLinkID++;
        System.out.println("A new link has been created connecting Node " + mSourceNode.getRouterID() + " to Node " + mDestinationNode.getRouterID() + " over Link " + mLinkID);
    }

    public void setCost(int cost) {
        mCost = cost;
    }
    public void setPort(int port){
        mConnectionPort = port;
    }
    public void setDestinationNode(Node mDestinationNode) {
        this.mDestinationNode = mDestinationNode;
    }
    public void setSourceNode(Node mSourceNode) {
        this.mSourceNode = mSourceNode;
    }
    public void setMessageToSend(String mMessageToSend) {
        this.mMessageToSend = mMessageToSend;
    }

    public Node getSourceNode() {
        return mSourceNode;
    }
    public Node getDestinationNode() {
        return mDestinationNode;
    }
    public int  getPort() {
        return mConnectionPort;
    }
    public int  getCost() {
        return mCost;
    }
    public int getLinkID(){
        return mLinkID;
    }

    public void run() {
        initializeConnection();
        transmitString();
    }

    private void initializeConnection(){

        try {
            mSocket = new Socket(Node.LOCAL_HOST, mConnectionPort);
            System.out.println("Link " + mLinkID + " as been initialized and Node " + mSourceNode.getRouterID() + " is connected to Node " + mDestinationNode.getRouterID() + " on address " + mSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void transmitString(){

        try {
            System.out.println("Node " + mSourceNode.getRouterID() + " is using NodeThis is Link " + mLinkID + " being used to transmit data.");

            OutputStream outToServer = mSocket.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("Hello from " + mSocket.getLocalSocketAddress());
            InputStream inFromServer = mSocket.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF());
            OutputStream outputStream = mSocket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(mMessageToSend);
            mSocket.close();
        } catch (IOException e) {

            System.out.println("IO Exception!");
            e.printStackTrace();
        }
    }

}