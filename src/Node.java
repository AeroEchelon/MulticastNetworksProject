import org.w3c.dom.stylesheets.LinkStyle;
import sun.security.ntlm.Server;

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
    public static final int         DEFAULT_RECEIVING_PACKET_RATE = 1;
    public static final String      LOCAL_HOST = "localhost";

    private int                     mRouterID;                  // Integer ID of router
    private Role                    mRole;                      // 0 for receiver, 1 for forwarder, 2 for source
    private InetAddress             mIPAddress;
    private int                     mReceivingPacketRate;       // The rate to receive packets

    private ArrayList<Link>         mLinks;                     // Destination links
    private ArrayList<RoutingEntry> mRoutingEntries;            // Routing entries.

    public enum Role{
        SOURCE, RECEIVER, FORWARDER
    }

    /**
     * A lazy constructor only requiring ID and role for node creation.
     *
     * @param routerID
     * @param role
     */
    public Node(int routerID, Role role){
        this(routerID, role, LOCAL_HOST, DEFAULT_RECEIVING_PACKET_RATE);
    }

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param role
     * @param stringAddressOfNode
     */
    public Node(int routerID, Role role, String stringAddressOfNode, int receivingPacketRate) {
        mRouterID = routerID;
        mRole = role;
        try{
            mIPAddress = InetAddress.getByName(stringAddressOfNode);
        }catch (UnknownHostException e){
            System.out.println("UnknownHostException occurred.");
            e.printStackTrace();
        }
        mReceivingPacketRate = receivingPacketRate;
        mLinks = new ArrayList<Link>(); // creating empty list
        mRoutingEntries = new ArrayList<RoutingEntry>();
    }

    /**
     *  This method is the last method that should be set after all node parameters have been initialized.
     *
     *  It is responsible for listening for incoming datagrams and forwarding outgoing datagrams.
     */
    public void initialize(){
        initalizeLinks();
    }

    private void initalizeLinks() {

        Iterator<Link> iterator = mLinks.iterator();

        while(iterator.hasNext()){
            Link link = iterator.next();
            link.run();
        }
    }

    public int getRouterID() {
        return mRouterID;
    }

    public Role getRole() {
        return mRole;
    }

    public int getReceivingPacketRate() {
        return mReceivingPacketRate;
    }

    public InetAddress getIPAddress() {
        return mIPAddress;
    }

    public ArrayList<Link> getDestinationLinks() {
        return mLinks;
    }

    public void addDestinationNode(Node destinationNode, int port){
        mLinks.add(new Link(this, destinationNode, port, Link.DEFAULT_COST));
    }

    public void addSourceNode(Node sourceNode, int port){
        mLinks.add(new Link(sourceNode, this, port, Link.DEFAULT_COST));
    }

    public void transmitDataToReceiverGivenRID(int routerID, String stringToSend) {

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

            if (routingEntry.getDestinationNode().getRouterID() == routerID){

                int nextHopID = routingEntry.getNextHopNode().getRouterID();

                Iterator<Link> linkIterator = mLinks.iterator();

                while(linkIterator.hasNext()){
                    Link link = linkIterator.next();

                    if (link.getDestinationNode().getRouterID() == nextHopID){
                        try{
                            link.transmitAString(stringToSend);
                        }catch (IOException e){
                            System.out.println("IO Exception occurred.");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}