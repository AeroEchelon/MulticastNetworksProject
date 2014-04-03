/**
 * Created by marvinbernal on 2014-03-27.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Provides a link between two nodes at the end of a link.
 *
 * This is essentially a high level socket class which includes socket creation and packet transmission. All links
 * are established on a background thread.
 *
 * The source node is assumed to be the 'CLIENT' while the destination node is assumed to be the 'SERVER' node.
 */
final class Link extends Thread{

    /* CONSTANTS */
    public static final int     SOCKET_TIMEOUT = 120000;    // Timeout in milliseconds
    public static final int     DEFAULT_COST = 1;           // Default cost for link

    /* PRIMARY ATTRIBUTES */
    private int                 mLinkID;
    private Node                mSourceNode;
    private Node                mDestinationNode;
    private int                 mCost;

    private static int          mLinkIDcounter = 1;         // Used as a global counter to have each
                                                            // link contain a unique ID
    private String              mMessageToSend;

    /**
     * Constructs a link containing the 'source node', 'destination node'.
     *
     * These nodes are the end points of a link.
     *
     * @param sourceNode        The node at the beginning of the link.
     * @param destinationNode   The node at the end of the link.
     */
    public Link(Node sourceNode, Node destinationNode) {
        mSourceNode = sourceNode;
        mDestinationNode = destinationNode;
        mCost = Link.DEFAULT_COST;
        mLinkID = mLinkIDcounter++; // Each link contains a self-incrementing unique ID

        System.out.println("Link " + mLinkID + " <SOURCE[Node " + mSourceNode.getNodeID()
                + ", IP " + sourceNode.getIPAddress() + "], DESTINATION[Node "
                + mDestinationNode.getNodeID() + ", IP " + destinationNode.getIPAddress()
                + ", Port " + destinationNode.getListeningPort() + "], [Cost " + mCost + "]>");
    }

    /**
     *  Overrides the Thread run() function to call this method on a different thread.
     */
    @Override
    public void run() {
        transmitString();
    }

    /**
     * Transmits a message over a link.
     *
     * Explicitly, this link is used to create a socket connection from one node to another. Because this link
     * is a thread all IO is handled without blocking the main process. After the message is sent, the socket connection
     * is closed.
     */
    private void transmitString(){
        try {
            // Creating a new socket
            Socket socket = new Socket(Node.LOCAL_HOST, getDestinationNode().getListeningPort());

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

    public void setCost(int cost) {
        mCost = cost;
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

    public int  getCost() {
        return mCost;
    }

    public int  getLinkID(){
        return mLinkID;
    }

}