import java.io.IOException;
import java.net.*;
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

    /* CONSTANTS */
    public static final int         DEFAULT_PORT = 6066;        // Default listening port
    public static final int         SOCKET_TIMEOUT = 120000;    // Timeout in milliseconds

    public static final int         DEFAULT_COST = 1;           // Default cost for link
    public static final int         DEFAULT_RECEIVING_PACKET_RATE = 1;
    public static final String      LOCAL_HOST = "localhost";

    private int                     mRouterID;                  // Integer ID of router
    private Role                    mRole;                      // 0 for receiver, 1 for forwarder, 2 for source
    private InetAddress             mIPAddress;
    private int                     mReceivingPacketRate;       // The rate to receive packets

    private int                     mListeningPort;             // The port to listen to incoming connections

    private ArrayList<Link>         mLinks;                     // Destination links
    private ArrayList<RoutingEntry> mRoutingEntries;            // Routing entries.

    private ServerSocket            mServerSocket;     // Socket to listen for incoming connections
    private Socket                  mSocket;

    private String                  mIncomingMessage;           // Incoming Message

    public enum Role{
        SOURCE, RECEIVER, FORWARDER
    }

    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * @param routerID      Router ID.
     * @param role          Role of router.
     * @param listeningPort Port to listen for incoming connections.
     */
    public Node(int routerID, Role role, int listeningPort){
        this(routerID, role, LOCAL_HOST, listeningPort, DEFAULT_RECEIVING_PACKET_RATE);
    }

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param role
     * @param stringAddressOfNode
     */
    public Node(int routerID, Role role, String stringAddressOfNode, int listeningPort, int receivingPacketRate) {
        mRouterID = routerID;
        mRole = role;
        mListeningPort = listeningPort;
        mReceivingPacketRate = receivingPacketRate;
        mLinks = new ArrayList<Link>(); // creating empty list
        mRoutingEntries = new ArrayList<RoutingEntry>();

        try{
            mIPAddress = InetAddress.getByName(stringAddressOfNode);
        }catch (UnknownHostException e){
            System.out.println("UnknownHostException occurred.");
            e.printStackTrace();
        }

        try {
            mServerSocket = new ServerSocket(getListeningPort());
            mServerSocket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Upon creating this node will add itself as a known destination node
//        this.addDestinationNode(this);
//        this.addRoutingEntry(this, this);

        System.out.println("Node " + mRouterID + " <Role " + mRole + ", Listening Port " + mListeningPort + ", Receiving Packet Rate " + mReceivingPacketRate + ">");
    }


    /**
     *  This method is the last method that should be set after all node parameters have been initialized.
     *
     *  It is responsible for listening for incoming datagrams and forwarding outgoing datagrams.
     */
    public abstract void initialize();

    public int getRouterID() {
        return mRouterID;
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

    public void setRouterID(int mRouterID) {
        this.mRouterID = mRouterID;
    }

    public void setRole(Role mRole) {
        this.mRole = mRole;
    }

    public void setIPAddress(InetAddress mIPAddress) {
        this.mIPAddress = mIPAddress;
    }

    public void setReceivingPacketRate(int mReceivingPacketRate) {
        this.mReceivingPacketRate = mReceivingPacketRate;
    }

    public void setListeningPort(int mListeningPort) {
        this.mListeningPort = mListeningPort;
    }

    public void setLinks(ArrayList<Link> mLinks) {
        this.mLinks = mLinks;
    }

    public void setRoutingEntries(ArrayList<RoutingEntry> mRoutingEntries) {
        this.mRoutingEntries = mRoutingEntries;
    }

    public void setServerSocket(ServerSocket listeningSocket) {
        mServerSocket = listeningSocket;
    }

    public void setSocket(Socket socket) {
        mSocket = socket;
    }

    public void setIncomingMessage(String mIncomingMessage) {
        this.mIncomingMessage = mIncomingMessage;
    }

    public void addDestinationNode(Node destinationNode){

        Iterator<Link> linkIterator = mLinks.iterator();

        while(linkIterator.hasNext()){
            Link link = linkIterator.next();

            if(link.getDestinationNode().getRouterID() == destinationNode.getRouterID()){
                // link to destination node already exists update link information

                System.out.println("Link from Node " + mRouterID + " to Node " + destinationNode.getRouterID() + " already exists. Updating information");
                link.setDestinationNode(destinationNode);
                link.setPort(destinationNode.getListeningPort());
                link.setSourceNode(this);
                return; // break
            }
        }

        // Will add new destination node if existing link is not found
        mLinks.add(new Link(this, destinationNode, destinationNode.getListeningPort(), Link.DEFAULT_COST));
    }

    public void addRoutingEntry(Node nextHopNode, Node destinationNode){

        Iterator<RoutingEntry> routingEntryIterator = mRoutingEntries.iterator();
        Boolean duplicateRoutingEntryNotFound = true;
        Boolean linkNotFound = true;

        while(routingEntryIterator.hasNext()){
            RoutingEntry routingEntry = routingEntryIterator.next();

            if(routingEntry.getDestinationNode().getRouterID() == destinationNode.getRouterID()){
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
            Link linkToUse = new Link();

            Iterator<Link> linkIterator = mLinks.iterator();

            while(linkIterator.hasNext()){

                Link link = linkIterator.next();

                if(link.getDestinationNode().getRouterID() == nextHopNode.getRouterID()){
                    System.out.println("Node " + mRouterID + " Routing Entry <[Link " + link.getLinkID() + "], [Next Hop Node " + nextHopNode.getRouterID() + " @ " + nextHopNode.getIPAddress() + "], [Destination Node " + destinationNode.getRouterID() + " @ " + destinationNode.getIPAddress() +"]>");
                    linkNotFound = false;
                    linkToUse = link;
                }

            }

            RoutingEntry routingEntry = new RoutingEntry(nextHopNode, destinationNode, linkToUse);
            mRoutingEntries.add(routingEntry);
            return;
        }

        if(linkNotFound){
            System.out.println("No physical link from Node " + mRouterID + " to Node " +destinationNode.getRouterID() + " has been established.");
        }
    }

    public void forwardMessageOverConnection(int destinationRouterID, String stringToSend) {

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

            if (routingEntry.getDestinationNode().getRouterID() == destinationRouterID){

                int nextHopID = routingEntry.getNextHopNode().getRouterID();

                Iterator<Link> linkIterator = mLinks.iterator();

                while(linkIterator.hasNext()){
                    Link link = linkIterator.next();

                    if (link.getDestinationNode().getRouterID() == nextHopID){
                        System.out.println("<Node " + mRouterID + " @ " + mIPAddress + " on port " + link.getDestinationNode().getListeningPort() + "> is using Link " + link.getLinkID() + " to transmit message: \"" + stringToSend + "\" to Node " + link.getDestinationNode().getRouterID() + " listening on port " + link.getDestinationNode().getListeningPort());
                        link.setMessageToSend(stringToSend);
                        link.run();
                        return;
                    }
                }

                System.out.println("No link found for Node " + mRouterID + " connecting it to Node " + destinationRouterID);

            }
        }

        System.out.println("No routing entry found in Node " + mRouterID + " for destination Node " + destinationRouterID);
    }
}