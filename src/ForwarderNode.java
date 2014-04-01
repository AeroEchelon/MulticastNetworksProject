import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created by marvinbernal on 2014-03-31.
 *
 * A node which forwards packets from incoming nodes to other nodes.
 *
 * Specifically this node can receive packets from the source or other forwarders and forward them to other forwarders
 * or receiver nodes.
 *
 */
final class ForwarderNode extends Node{

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param listeningPort
     * @param receivingPacketRate
     */
    public ForwarderNode(double routerID, String stringAddressOfNode, int listeningPort, int receivingPacketRate) {
        super(routerID, Role.FORWARDER, stringAddressOfNode, listeningPort, receivingPacketRate);

    }
    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * IP Address and packet rate are set to default.
     *
     * @param routerID      Router ID.
     * @param listeningPort Port to listen for incoming connections.
     */
    public ForwarderNode(double routerID, int listeningPort) {
        this(routerID, Node.LOCAL_HOST, listeningPort, Node.DEFAULT_RECEIVING_PACKET_RATE);
    }

    /**
     * Initialize will begin to listen for incoming connections and proceed to forward it to the next hop node as denoted
     * in the packet structure.
     */
    @Override
    public void initialize() {
        new Thread(){
            @Override
            public void run(){

                while(true){
                    try{
                        setSocket(getServerSocket().accept()); // blocks until a connection is made
                        DataInputStream in = new DataInputStream(getSocket().getInputStream());
                        String incomingMessage = in.readUTF();

                        StringTokenizer tokenPacket = new StringTokenizer(incomingMessage,", ");

                        // Example: C, 2
                        // Which means Controller, Link ID 2
                        String typeOfNodeReceivedFrom = tokenPacket.nextToken();
                        String nodeIdToAddRoutingEntryFor = tokenPacket.nextToken();

                        if(typeOfNodeReceivedFrom.equals("C")){

                            Iterator<Link> linkIterator = getLinks().iterator();

                            while(linkIterator.hasNext()){
                                Link link = linkIterator.next();

                                if((link.getDestinationNode().getRouterID() == Double.parseDouble(nodeIdToAddRoutingEntryFor)) && (link.getDestinationNode().getRole() == Role.FORWARDER)){
                                    // Adding forwarder node to routing table
                                    addRoutingEntry(link.getDestinationNode(), link.getDestinationNode());
                                }
                            }

                        }else if(typeOfNodeReceivedFrom.equals(SourceNode.REQUEST_TO_FORWARDER)){
                            // Will be expecting a response from SOURCE

                        }else if(typeOfNodeReceivedFrom.equals(ReceiverNode.REQUEST_TO_FORWARDER)){

                            /**
                             * Based on the receiver response, this node will iterate through each of it's links
                             * and find the destination node ID that matches this receiver.
                             *
                             * Once it finds this information it adds a routing entry to itself with the next hop
                             * node and destination node equal to the receiver node.
                             */

                            Iterator<Link> linkIterator = getLinks().iterator();

                            while(linkIterator.hasNext()){
                                Link link = linkIterator.next();

                                if(link.getDestinationNode().getRouterID() == Double.parseDouble(nodeIdToAddRoutingEntryFor)){
                                    addRoutingEntry(link.getDestinationNode(), link.getDestinationNode());

                                }
                            }

                        }else{

                        System.out.println("<Node " + getRouterID() + " @ " + getIPAddress() + " receives message " + incomingMessage + "\" from remote address " + getSocket().getRemoteSocketAddress());

                        DataOutputStream out = new DataOutputStream(getSocket().getOutputStream());
                        out.writeUTF("--- Node " + getRouterID() + " acknowledges message from " + getSocket().getRemoteSocketAddress());

                        // Forward Data
                        sendPacket(Integer.parseInt(incomingMessage), incomingMessage);
                        getSocket().close();

                        }

                    }catch(SocketTimeoutException socketTimeoutException){

                        System.out.println("Socket timed out!");

                    }catch(IOException iOException){
                        iOException.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * Sends all link information to destination node.
     */
    @Override
    public void configureRoutingTable() {
        initialize();
    }
}
