/**
 * Created by marvinbernal on 2014-03-27.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Provides a link between nodes.
 *
 * This includes socket creation and packet transmission.
 *
 * The source node is assumed to be the 'CLIENT' while the destination node is assumed to be the 'SERVER' node.
 */
final class Link extends Thread{

    public static final int     DEFAULT_PORT = 6066;        // Default listening port
    public static final int     SOCKET_TIMEOUT = 120000;    // Timeout in milliseconds

    public static final int     DEFAULT_COST = 1;           // Default cost for link

    private Node                mSourceNode;
    private Node                mDestinationNode;
    private int                 mConnectionPort;
    private int                 mCost;
    private static int          mLinkIDcounter = 1;
    private int                 mLinkID;

    private String              mMessageToSend;

    public Link(Node sourceNode, Node destinationNode, int connectionPort) {

        mSourceNode = sourceNode;
        mDestinationNode = destinationNode;
        mConnectionPort = connectionPort;
        mCost = Link.DEFAULT_COST;
        mLinkID = mLinkIDcounter++;
        System.out.println("Link " + mLinkID + " <SOURCE[Node " + mSourceNode.getRouterID() + ", IP " + sourceNode.getIPAddress() + "], DESTINATION[Node " + mDestinationNode.getRouterID() + ", IP " + destinationNode.getIPAddress() + ", Port " + mConnectionPort + "], [Cost " + mCost + "]>");
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
        transmitString();
    }

    public void transmitString(){
        try {

            Socket socket = new Socket(Node.LOCAL_HOST, mConnectionPort);

            // Transmitting message
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(mMessageToSend);

            // Receiving acknowledgement
            DataInputStream mDataInputStream = new DataInputStream(socket.getInputStream());
            System.out.println(mDataInputStream.readUTF());
            socket.close();
        } catch (IOException e) {

            System.out.println("IO Exception!");
            e.printStackTrace();
        }
    }

}