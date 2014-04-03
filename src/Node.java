import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by marvinbernal on 2014-03-26.
 *
 * An abstract node that contains attributes pertaining to source / destination IP addresses, receiving packet rate
 * and a list of methods for node connection.
 *
 * Each node is essentially a 'server' listening for incoming links on a background thread.
 */
abstract class Node {

    /* CONSTANTS */
    public static final int         SOCKET_TIMEOUT = 120000;    // Timeout in milliseconds
    public static final int         DEFAULT_RECEIVING_PACKET_RATE = 100;
    public static final String      LOCAL_HOST = "localhost";

    /* PRIMARY ATTRIBUTES */
    private int                     mNodeID;                    // Integer ID of node
    private Role                    mRole;                      // 0 for receiver, 1 for forwarder, 2 for source
    private InetAddress             mIPAddress;
    private int                     mReceivingPacketRate;       // The rate to receive packets

    private int                     mListeningPort;             // The port to listen to incoming connections

    /* LINK AND ROUTING ATTRIBUTES */
    private ArrayList<Link>         mLinks;                     // Destination links
    private ArrayList<RoutingEntry> mRoutingEntries;            // Routing entries.
    private ServerSocket            mServerSocket;              // Socket to listen for incoming connections
    private Socket                  mSocket;

    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * @param nodeID        The ID of the node.
     * @param role          The role of the router.
     * @param listeningPort Listening port for incoming socket connections.
     */
    public Node(int nodeID, Role role, int listeningPort){
        this(nodeID, role, LOCAL_HOST, listeningPort, DEFAULT_RECEIVING_PACKET_RATE);
    }

    /**
     * Primary node constructor.
     *
     * @param nodeID                The ID of the node.
     * @param role                  The role of the node in the network.
     * @param IPAddress             The IPAddress of the node.
     * @param listeningPort         The listening port used to listen for incoming connections.
     * @param receivingPacketRate   The packet rate at which this node can recieving incoming packets.
     */
    public Node(int nodeID, Role role, String IPAddress, int listeningPort, int receivingPacketRate) {
        mNodeID = nodeID;
        mRole = role;
        mListeningPort = listeningPort;
        mReceivingPacketRate = receivingPacketRate;
        mLinks = new ArrayList<Link>(); // creating empty list
        mRoutingEntries = new ArrayList<RoutingEntry>();

        try{
            mIPAddress = InetAddress.getByName(IPAddress);
        }catch (UnknownHostException e){
            System.out.println("UnknownHostException occurred.");
            e.printStackTrace();
        }

        try {
            mServerSocket = new ServerSocket(getListeningPort());
            mServerSocket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (IOException e) {
            System.out.println("IO Exception has occurred.");
            e.printStackTrace();
        }

        System.out.println("Node " + mNodeID + " <Role " + mRole + ", Listening Port " + mListeningPort + ", Receiving Packet Rate " + mReceivingPacketRate + ">");
    }

    /**
     *  This method is the last method that should be set after all node parameters have been initialized.
     *
     *  It is responsible for listening for incoming packets and forwarding outgoing packets.
     */
    public abstract void initialize();

    /**
     * Will create a link from this node to the destination node. It first checks if this connection already
     * exists and if so will update the link information instead.
     *
     * @param destinationNode - The next hop / destination node.
     */
    public void addDestinationNode(Node destinationNode){

        Iterator<Link> linkIterator = mLinks.iterator();

        while(linkIterator.hasNext()){
            Link link = linkIterator.next();

            if(link.getDestinationNode().getNodeID() == destinationNode.getNodeID()){
                // link to destination node already exists update link information

                System.out.println("Link from Node " + mNodeID + " to Node " + destinationNode.getNodeID() + " already exists. Updating information");
                link.setDestinationNode(destinationNode);
                link.setSourceNode(this);
                return; // break
            }
        }

        // Will add new destination node if existing link is not found
        mLinks.add(new Link(this, destinationNode));
    }

    /**
     * Adds a routing entry to a destination node given its next hop node.
     *
     * If an entry to a destination already exists, it updates that entry with the latest next hop node.
     *
     * @param nextHopNode       The next hop node.
     * @param destinationNode   The destination node.
     */
    public void addRoutingEntry(Node nextHopNode, Node destinationNode){

        Iterator<RoutingEntry> routingEntryIterator = mRoutingEntries.iterator();
        Boolean duplicateRoutingEntryNotFound = true;
        Boolean linkNotFound = true;

        while(routingEntryIterator.hasNext()){
            RoutingEntry routingEntry = routingEntryIterator.next();

            if(routingEntry.getDestinationNode().getNodeID() == destinationNode.getNodeID()){
                // duplicate entry for destination route already in routing table, update routing entry
                duplicateRoutingEntryNotFound = false;
                routingEntry.setNextHopNode(nextHopNode);
            }

        }

        /**
         *  If no entry to destination node was found in this node's entry table
         *  then find a link to corresponding node, and creating a routing entry to desintation node
         */
        if(duplicateRoutingEntryNotFound){

            Iterator<Link> linkIterator = mLinks.iterator();

            while(linkIterator.hasNext()){

                Link link = linkIterator.next();

                if(link.getDestinationNode().getNodeID() == nextHopNode.getNodeID()){
                    System.out.println("Node " + mNodeID + " Routing Entry <[Link " + link.getLinkID()
                            + "], [Next Hop Node " + nextHopNode.getNodeID() + " @ " + nextHopNode.getIPAddress()
                            + "], [Destination Node " + destinationNode.getNodeID() + " @ "
                            + destinationNode.getIPAddress() +"]>");
                    linkNotFound = false;

                    RoutingEntry routingEntry = new RoutingEntry(link, destinationNode);
                    mRoutingEntries.add(routingEntry);
                }

            }

        }

        if(linkNotFound){
            System.out.println("No physical link from Node " + mNodeID + " to Node "
                    + destinationNode.getNodeID() + " has been established.");
        }
    }

    /**
     * Sends a message from this node to a destination node. This desintation node does not have to be directly
     * connected to this node. Given the destination ID, it will look up in its routing table if it has a path
     * to the next hop node.
     *
     * If neither a link or routing entry is found, a message is thrown and the message is *not* sent.
     *
     * @param destinationRouterID   The node ID to receive message.
     * @param stringToSend          The message to send.
     */
    public void sendMessageGivenRouterID(int destinationRouterID, String stringToSend) {

        /**
         * IMPLEMENTATION
         *
         * Checks routing table for destination node by ID. During match, will grab the corresponding
         * next hop node and takes note of this ID.
         *
         * Next will iterate through all of the links this node is connected to and check the destination node
         * for all links. If match will transmit data on this link.
         */


        Iterator<RoutingEntry> routingEntryIterator = mRoutingEntries.iterator();

        while(routingEntryIterator.hasNext()){
            RoutingEntry routingEntry = routingEntryIterator.next();

            if (routingEntry.getDestinationNode().getNodeID() == destinationRouterID){

                int nextHopID = routingEntry.getNextHopNode().getNodeID();

                Iterator<Link> linkIterator = mLinks.iterator();

                while(linkIterator.hasNext()){
                    Link link = linkIterator.next();

                    if (link.getDestinationNode().getNodeID() == nextHopID){
                        System.out.println("<Node " + mNodeID + " @ " + mIPAddress + " on port "
                                + link.getDestinationNode().getListeningPort() + "> is using Link " + link.getLinkID()
                                + " to transmit message: \"" + stringToSend + "\" to Node "
                                + link.getDestinationNode().getNodeID()
                                + " listening on port " + link.getDestinationNode().getListeningPort());
                        link.setMessageToSend(stringToSend);
                        link.run();
                        return;
                    }
                }

                System.out.println("No link found for Node " + mNodeID + " connecting it to Node " + destinationRouterID);

            }
        }

        System.out.println("No routing entry found in Node " + mNodeID + " for destination Node " + destinationRouterID);
    }

    public int getNodeID() {
        return mNodeID;
    }

    public Role getRole() {
        return mRole;
    }

    public InetAddress getIPAddress() {
        return mIPAddress;
    }

    public int getReceivingPacketRate() {
        return mReceivingPacketRate;
    }

    public void setReceivingPacketRate(int mReceivingPacketRate) {
        this.mReceivingPacketRate = mReceivingPacketRate;
    }

    public int getListeningPort() {
        return mListeningPort;
    }

    public ArrayList<Link> getLinks() {
        return mLinks;
    }

    public ArrayList<RoutingEntry> getRoutingEntries() {
        return mRoutingEntries;
    }

    public ServerSocket getServerSocket() {
        return mServerSocket;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void setSocket(Socket socket) {
        mSocket = socket;
    }

    /**
     * Each Node can either be a SOURCE, FORWARDER or RECEIVER
     */
    public enum Role{
        SOURCE, FORWARDER, RECEIVER
    }
}