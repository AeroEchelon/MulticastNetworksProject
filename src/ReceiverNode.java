import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by marvinbernal on 2014-03-30.
 */
final class ReceiverNode extends Node {

    private ArrayList<Integer> mListOfGroupMembershipIDs;

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param stringAddressOfNode
     * @param listeningPort
     * @param receivingPacketRate
     */
    public ReceiverNode(double routerID, String stringAddressOfNode, int listeningPort, int receivingPacketRate, ArrayList<Integer> groupIDs) {
        super(routerID, Role.RECEIVER, stringAddressOfNode, listeningPort, receivingPacketRate);
        mListOfGroupMembershipIDs = groupIDs;
    }

    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * IP Address and packet rate are set to default.
     *
     * @param listeningPort Port to listen for incoming connections.
     */
    public ReceiverNode(int routerID, int listeningPort, ArrayList<Integer> defaultGroupIDs) {
        this(routerID, Node.LOCAL_HOST, listeningPort, Node.DEFAULT_RECEIVING_PACKET_RATE, defaultGroupIDs);
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

                        System.out.println("<Node " + getRouterID() + " @ " + getIPAddress() + " has accepted connection and is connected to remote address " + getSocket().getRemoteSocketAddress());

                        DataInputStream in = new DataInputStream(getSocket().getInputStream());

                        String incomingMessage = in.readUTF();
                        System.out.println("<Node " + getRouterID() + " @ " + getIPAddress() + " receives message " + incomingMessage + "\" from remote address " + getSocket().getRemoteSocketAddress());

                        DataOutputStream out = new DataOutputStream(getSocket().getOutputStream());
                        out.writeUTF("--- Node " + getRouterID() + " acknowledges message from " + getSocket().getRemoteSocketAddress());
                        getSocket().close();

                    }catch(SocketTimeoutException socketTimeoutException){

                        System.out.println(" Socket timed out!");

                    }catch(IOException iOException){
                        iOException.printStackTrace();
                    }catch (Exception e){
                        break;
                    }
                }
            }
        }.start();
    }
}