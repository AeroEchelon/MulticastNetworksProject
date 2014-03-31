import org.w3c.dom.stylesheets.LinkStyle;
import sun.security.ntlm.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
public class Node {

    /* CONSTANTS */
    public static final int         DEFAULT_PORT = 6066;        // Default listening port
    public static final int         SOCKET_TIMEOUT = 120000;    // Timeout in milliseconds

    public static final int         DEFAULT_COST = 1; // Default cost for link
    public static final int         DEFAULT_RECEIVING_PACKET_RATE = 1;
    public static final String      LOCAL_HOST = "localhost";

    protected int                     mRouterID;                  // Integer ID of router
    protected Role                    mRole;                      // 0 for receiver, 1 for forwarder, 2 for source
    protected InetAddress             mIPAddress;
    protected int                     mReceivingPacketRate;       // The rate to receive packets

    protected int                     mListeningPort;             // The port to listen to incoming connections

    protected ArrayList<Link>         mLinks;                     // Destination links
    protected ArrayList<RoutingEntry> mRoutingEntries;            // Routing entries.

    protected ServerSocket            mServerListeningSocket;     // Socket to listen for incoming connections
    protected Socket                  mServerSocket;

    protected String                  mIncomingMessage;           // Incoming Message

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
    }


    /**
     *  This method is the last method that should be set after all node parameters have been initialized.
     *
     *  It is responsible for listening for incoming datagrams and forwarding outgoing datagrams.
     */
    public void initialize(){
        listenForConnections();
    }

    private void listenForConnections(){
        new Thread(){
            @Override
            public void run(){

                while(true){
                    try{
                        mServerListeningSocket = new ServerSocket(mListeningPort);
                        mServerListeningSocket.setSoTimeout(SOCKET_TIMEOUT);
                        mServerSocket =  mServerListeningSocket.accept(); // blocks until a connection is made

                        System.out.println("Node " + mRouterID + " has accepted and is acting as server and is connected to remote address " + mServerSocket.getRemoteSocketAddress());

                        DataInputStream in = new DataInputStream(mServerSocket.getInputStream());

                        mIncomingMessage = in.readUTF();
                        System.out.println("Mesage received by Node " + mRouterID + " is \"" + mIncomingMessage + "\"");

                        DataOutputStream out = new DataOutputStream(mServerSocket.getOutputStream());
                        out.writeUTF("Node " + mRouterID + " acknowledges message from" + mServerSocket.getRemoteSocketAddress());


                        // Forward Data
                        forwardMessageOverConnection(Integer.parseInt(mIncomingMessage), mIncomingMessage);
                        mServerSocket.close();
                    }catch(SocketTimeoutException socketTimeoutException){

                        System.out.println("Socket timed out!");

                    }catch(IOException iOException){
                        iOException.printStackTrace();
                    }finally {
                        break;
                    }
                }
            }
        }.start();
    }
    public void forwardMessageOverConnection(int routerID, String stringToSend) {

        /**
         * IMPLEMENTATION
         *
         * Checks routing table for destination node by ID. During match, will grab the corresponding
         * next hop node and takes note of this ID.
         *
         * Next will iterate through all of the links this node is connected to and check the destination node
         * for all links. If match will transmit data on this link.
         */

        System.out.print("Node " + mRouterID + " is attempting to transmit data ... ");
        Iterator<RoutingEntry> routingEntryIterator = mRoutingEntries.iterator();

        while(routingEntryIterator.hasNext()){
            RoutingEntry routingEntry = routingEntryIterator.next();

            if (routingEntry.getDestinationNode().getRouterID() == routerID){

                int nextHopID = routingEntry.getNextHopNode().getRouterID();

                Iterator<Link> linkIterator = mLinks.iterator();

                while(linkIterator.hasNext()){
                    Link link = linkIterator.next();

                    if (link.getDestinationNode().getRouterID() == nextHopID){
                        System.out.println("Found and transmitting data over link " + link.getLinkID() + " and sending message: " + stringToSend);
                        link.setMessageToSend(stringToSend);
                        link.start();
                    }
                }
            }
        }
    }



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
    public ServerSocket getServerListeningSocket() {
        return mServerListeningSocket;
    }
    public Socket getServerSocket() {
        return mServerSocket;
    }

    public void addDestinationNode(Node destinationNode){

        Iterator<Link> linkIterator = mLinks.iterator();

        while(linkIterator.hasNext()){
            Link link = linkIterator.next();

            if(link.getDestinationNode().getRouterID() == destinationNode.getRouterID()){
                // link to destination node already exists update link information

                System.out.println("Updating link information ...");
                link.setDestinationNode(destinationNode);
                link.setPort(destinationNode.getListeningPort());
                link.setSourceNode(this);
                return;
            }
        }

        // Will add new destination node if existing link is not found
        mLinks.add(new Link(this, destinationNode, destinationNode.getListeningPort(), Link.DEFAULT_COST));
    }

    public void addRoutingEntry(Node nextHopNode, Node destinationNode){
        Link linkToUse = new Link();

        Iterator<Link> linkIterator = mLinks.iterator();

        while(linkIterator.hasNext()){

            Link link = linkIterator.next();


            if(link.getDestinationNode().getRouterID() == nextHopNode.getRouterID()){
                System.out.println("Using Link " + link.getLinkID() + " in Node " + mRouterID + " and is now adding routing entry for destination Node " + destinationNode.getRouterID());

                linkToUse = link;
            }

        }

        RoutingEntry routingEntry = new RoutingEntry(nextHopNode, destinationNode, linkToUse);
        mRoutingEntries.add(routingEntry);
    }
}