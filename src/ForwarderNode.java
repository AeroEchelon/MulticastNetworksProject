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
     * @param role          Role of router.
     * @param listeningPort Port to listen for incoming connections.
     */
    public ForwarderNode(int routerID, int listeningPort) {
        super(routerID, Role.FORWARDER, listeningPort);
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
                        System.out.println("Setting listening port for Node " + getRouterID());
                        setServerListeningSocket(new ServerSocket(getListeningPort()));
                        getServerListeningSocket().setSoTimeout(SOCKET_TIMEOUT);

                        System.out.println("Listening for client to connect to Node " + getRouterID());
                        setServerSocket(getServerListeningSocket().accept()); // blocks until a connection is made

                        System.out.println("Node " + getRouterID() + " has accepted and is acting as server and is connected to remote address " + getServerSocket().getRemoteSocketAddress());

                        DataInputStream in = new DataInputStream(getServerSocket().getInputStream());

                        String incomingMessage = in.readUTF();
                        System.out.println("Message received by Node " + getRouterID() + " is \"" + incomingMessage + "\"");

                        DataOutputStream out = new DataOutputStream(getServerSocket().getOutputStream());
                        out.writeUTF("Node " + getRouterID() + " acknowledges message from" + getServerSocket().getRemoteSocketAddress());

                        // Forward Data
                        forwardMessageOverConnection(Integer.parseInt(incomingMessage), incomingMessage);
                        getServerSocket().close();
                        getServerListeningSocket();

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
}
