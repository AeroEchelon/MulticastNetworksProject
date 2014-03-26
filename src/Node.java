import java.net.Inet4Address;
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
public abstract class Node {

    private int routerID;               // Integer ID of router
    private int role;                   // 0 for receiver, 1 for forwarder, 2 for source
    private InetAddress IPAddress;      // IP Address specified in String notation
    private int receivingPacketRate;    // The rate to receive packets

    private int receivePort;
    private int forwardPort;

    private InetAddress multicastGroupInetAddress;

    // Use for node connections
    private ArrayList<Node> sourceNodes;
    private ArrayList<Node> destinationNodes;

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param role
     * @param stringAddressOfNode
     * @param stringAddressOfMulticastGroup
     */
    public Node(int routerID, int role, String stringAddressOfNode, String stringAddressOfMulticastGroup, int receivingPacketRate) throws UnknownHostException {
        this.routerID = routerID;
        this.role = role;
        this.IPAddress = InetAddress.getByName(stringAddressOfNode);
        this.receivingPacketRate = receivingPacketRate;
    }

    /**
     * Constructs a node with a default receiving packet rate of zero.
     *
     * @param routerID

     * @param role
     * @param IPAddress
     */
    public Node(int routerID, int role, String stringAddressOfNode, String stringAddressOfMulticastGroup) throws UnknownHostException {
        this(routerID, role, IPAddress, 0);
    }

    /* GETTERS */

    public ArrayList<Node> getDestinationNodes() {
        return destinationNodes;
    }

    public int getRouterID() {
        return routerID;
    }

    public int getRole() {
        return role;
    }

    public InetAddress getIPAddress() {
        return IPAddress;
    }

    public int getReceivingPacketRate() {
        return receivingPacketRate;
    }

    public ArrayList<Node> getSourceNodes() {
        return sourceNodes;
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

        InetAddress multicastGroupInetAddress = node.getIPAddress();
    }

    /**
     * Add node to list of source nodes.
     * @param node
     */
    public void addSourceNode(Node node, int destinationPort){
        sourceNodes.add(node);
    }

}