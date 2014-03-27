import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by marvinbernal on 2014-03-26.
 *
 * A generic node that contains attributes pertaining to source / destination IP addresses, receiving packet rate
 * and a list of methods for node connection
 *
 */
public abstract class Node extends Thread {

    public static final int     GENERIC_PORT = 9876;        // Default listening port
    public static final int     SOCKET_TIMEOUT = 100000;    // Timeout in miliseconds

    private int                 mRouterID;                  // Integer ID of router
    private int                 mRole;                      // 0 for receiver, 1 for forwarder, 2 for source
    private InetAddress         mIPAddress;
    private int                 mReceivingPacketRate;       // The rate to receive packets

    // Use for node connections
    private ArrayList<Node>     sourceNodes;
    private ArrayList<Node>     destinationNodes;

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param role
     * @param stringAddressOfNode
     */
    public Node(int routerID, int role, String stringAddressOfNode, int receivingPacketRate) throws UnknownHostException {
        mRouterID = routerID;
        mRole = role;
        mIPAddress = InetAddress.getByName(stringAddressOfNode);
        mReceivingPacketRate = receivingPacketRate;
    }

    /**
     *  This method is the last method that should be set after all node parameters have been initialized.
     *
     *  It is responsible for listening for incoming datagrams and forwarding outgoing datagrams.
     */
     abstract void initialize() throws IOException;

    public int getRouterID() {
        return mRouterID;
    }

    public int getRole() {
        return mRole;
    }

    public int getReceivingPacketRate() {
        return mReceivingPacketRate;
    }

    public InetAddress getIPAddress() {
        return mIPAddress;
    }

    public ArrayList<Node> getSourceNodes() {
        return sourceNodes;
    }

    public ArrayList<Node> getDestinationNodes() {
        return destinationNodes;
    }

    /**
     * This will remove the node in the list of source nodes specified by @rid
     * @param rid The ID of the router to be removed.
     */
    public void removeSourceNodeByRID(int rid){
        Iterator<Node> iterator = destinationNodes.iterator();

        while(iterator.hasNext()){
            if(iterator.next().getRouterID() == rid){
                iterator.remove();
                break;
            }
        }
    }

    /**
     * This will remove the node in the list of destination nodes specified by @rid
     * @param rid The ID of the router to be removed.
     */
    public void removeDesintationNodeByRID(int rid){
        Iterator<Node> iterator = sourceNodes.iterator();

        while(iterator.hasNext()){
            if(iterator.next().getRouterID() == rid){
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Add node to list of destination nodes.
     * @param node
     */
    public void addDestinationNode(Node node, int sourcePort){
        destinationNodes.add(node);
    }

    /**
     * Add node to list of source nodes.
     * @param node
     */
    public void addSourceNode(Node node, int destinationPort){
        sourceNodes.add(node);
    }

}