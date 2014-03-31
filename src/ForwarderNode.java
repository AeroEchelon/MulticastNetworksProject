import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

/**
 * Created by marvinbernal on 2014-03-31.
 */
public final class ForwarderNode extends Node{


    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * @param routerID      Router ID.
     * @param listeningPort Port to listen for incoming connections.
     */
    public ForwarderNode(int routerID, int listeningPort) {
        this(routerID, Role.FORWARDER, Node.LOCAL_HOST, listeningPort, Node.DEFAULT_RECEIVING_PACKET_RATE);
    }

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param role
     * @param stringAddressOfNode
     * @param listeningPort
     * @param receivingPacketRate
     */
    public ForwarderNode(int routerID, Role role, String stringAddressOfNode, int listeningPort, int receivingPacketRate) {
        super(routerID, role, stringAddressOfNode, listeningPort, receivingPacketRate);


    }

    /**
     * This method is the last method that should be set after all node parameters have been initialized.
     * <p/>
     * It is responsible for listening for incoming datagrams and forwarding outgoing datagrams.
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
                        System.out.println("<Node " + getRouterID() + " @ " + getIPAddress() + " receives message " + incomingMessage + "\" from remote address " + getSocket().getRemoteSocketAddress());

                        DataOutputStream out = new DataOutputStream(getSocket().getOutputStream());
                        out.writeUTF("--- Node " + getRouterID() + " acknowledges message from " + getSocket().getRemoteSocketAddress());

                        // Forward Data
                        forwardMessageOverConnection(Integer.parseInt(incomingMessage), incomingMessage);
                        getSocket().close();

                    }catch(SocketTimeoutException socketTimeoutException){

                        System.out.println("Socket timed out!");

                    }catch(IOException iOException){
                        iOException.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
