import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by marvinbernal on 2014-03-30.
 *
 * A node only capable of receiving incoming packets.
 *
 */
final class ReceiverNode extends Node {

    /**
     * Primary node constructor.
     *
     * @param nodeID                The ID of the node.
     * @param IPAddress             The IPAddress of the node.
     * @param listeningPort         The listening port used to listen for incoming connections.
     * @param receivingPacketRate   The packet rate at which this node can recieving incoming packets.
     */
    public ReceiverNode(int nodeID, String IPAddress, int listeningPort, int receivingPacketRate) {
        super(nodeID, Role.RECEIVER, IPAddress, listeningPort, receivingPacketRate);
    }

    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * IP Address and packet rate are set to default.
     *
     * @param listeningPort Port to listen for incoming connections.
     */
    public ReceiverNode(int nodeID, int listeningPort) {
        super(nodeID, Role.RECEIVER, listeningPort);
    }

    /**
     * Initialize will initiate this node to listen for socket connections on its listening port.
     */
    @Override
    public void initialize() {
        new Thread(){
            @Override
            public void run(){

                while(true){
                    try{
                        setSocket(getServerSocket().accept()); // blocks until a connection is made

                        System.out.println("<Node " + getNodeID() + " @ " + getIPAddress()
                                + " has accepted connection and is connected to remote address "
                                + getSocket().getRemoteSocketAddress());

                        DataInputStream in = new DataInputStream(getSocket().getInputStream());

                        String incomingMessage = in.readUTF();
                        System.out.println("<Node " + getNodeID() + " @ " + getIPAddress()
                                + " receives message " + incomingMessage + "\" from remote address "
                                + getSocket().getRemoteSocketAddress());

                        DataOutputStream out = new DataOutputStream(getSocket().getOutputStream());
                        out.writeUTF("--- Node " + getNodeID() + " acknowledges message from "
                                + getSocket().getRemoteSocketAddress());
                        getSocket().close();

                    }catch(SocketTimeoutException socketTimeoutException){
                        System.out.println(" Socket timed out!");
                    }catch(IOException iOException){
                        iOException.printStackTrace();
                    }catch (Exception e){
                        System.out.println("An exception has occured.");
                        e.printStackTrace();
                        break;
                    }
                }

            }
        }.start();

    }
}
