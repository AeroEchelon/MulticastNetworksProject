import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
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
     * A lazy constructor only requiring ID and listening for node creation. This router is assumed to be created on the
     * local machine.
     *
     * @param nodeID      Node ID.
     * @param listeningPort Port to listen for incoming connections.
     */
    public ForwarderNode(int nodeID, int listeningPort) {
        this(nodeID, Node.LOCAL_HOST, listeningPort, Node.DEFAULT_RECEIVING_PACKET_RATE);
    }

    /**
     * Primary forward node constructor.
     *
     * @param nodeID                The ID of the node.
     * @param IPAddress             The IPAddress of the node.
     * @param listeningPort         The listening port used to listen for incoming connections.
     * @param receivingPacketRate   The packet rate at which this node can recieving incoming packets.
     */
    public ForwarderNode(int nodeID, String IPAddress,  int listeningPort, int receivingPacketRate) {
        super(nodeID, Role.FORWARDER, IPAddress, listeningPort, receivingPacketRate);
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
                        // Listening for incoming socket connections from other nodes
                        setSocket(getServerSocket().accept()); // blocks until a connection is made
                        DataInputStream in = new DataInputStream(getSocket().getInputStream());
                        String packet = in.readUTF();

                        // Tokenizing input packet to obtain destination and message
                        StringTokenizer tokenPacket = new StringTokenizer(packet,", ");
                        String packetDestination = tokenPacket.nextToken();
                        String message = tokenPacket.nextToken();

                        // Printing out status information to output
                        System.out.println("<Node " + getNodeID() + " @ " + getIPAddress()
                                + " receives message \"" + message + "\" from remote address "
                                + getSocket().getRemoteSocketAddress());

                        // Replies back on existing socket to client acknowledging this request.
                        DataOutputStream out = new DataOutputStream(getSocket().getOutputStream());
                        out.writeUTF("--- Node " + getNodeID() + " acknowledges message from "
                                + getSocket().getRemoteSocketAddress());

                        // Forward Data
                        sendMessageGivenRouterID(Integer.parseInt(packetDestination), packet);
                        getSocket().close();

                    }catch(SocketTimeoutException socketTimeoutException){

                        System.out.println("Socket timed out!");

                    }catch(IOException iOException){
                        iOException.printStackTrace();
                    }
                }

            }
        }.start(); // Listen in background via separate thread
    }
}
