import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

/**
 * Created by marvinbernal on 2014-03-30.
 */
public class ReceiverNode extends Node {
    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param stringAddressOfNode
     * @param listeningPort
     * @param receivingPacketRate
     */
    public ReceiverNode(int routerID, String stringAddressOfNode, int listeningPort, int receivingPacketRate) {
        super(routerID, Role.RECEIVER, stringAddressOfNode, listeningPort, receivingPacketRate);
    }

    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * @param routerID      Router ID.
     * @param listeningPort Port to listen for incoming connections.
     */
    public ReceiverNode(int routerID, int listeningPort) {
        super(routerID, Role.RECEIVER, listeningPort);
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

                        System.out.println(" Node " + getRouterID() + " has accepted and is acting as server and is connected to remote address " + getSocket().getRemoteSocketAddress());

                        DataInputStream in = new DataInputStream(getSocket().getInputStream());

                        String incomingMessage = in.readUTF();
                        System.out.println(" Message received by Node " + getRouterID() + " is \"" + incomingMessage + "\"");

                        DataOutputStream out = new DataOutputStream(getSocket().getOutputStream());
                        out.writeUTF(" Node " + getRouterID() + " acknowledges message from" + getSocket().getRemoteSocketAddress());
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
