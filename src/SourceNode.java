import java.io.*;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

/**
 * Created by marvinbernal on 2014-03-31.
 */
public class SourceNode extends Node{

    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * @param routerID      Router ID.
     * @param role          Role of router.
     * @param listeningPort Port to listen for incoming connections.
     */
    public SourceNode(int routerID, int listeningPort) {
        super(routerID, Role.SOURCE, listeningPort);
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
    public SourceNode(int routerID, Role role, String stringAddressOfNode, int listeningPort, int receivingPacketRate) {
        super(routerID, role, stringAddressOfNode, listeningPort, receivingPacketRate);
    }

    /**
     * This method is the last method that should be set after all node parameters have been initialized.
     * <p/>
     * It is responsible for listening for incoming datagrams and forwarding outgoing datagrams.
     */
    @Override
    public void initialize() {

        while(true){

            System.out.println("Enter something here : ");

            String messageToSend = "";
            try{
                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                messageToSend = bufferRead.readLine();

                System.out.println("This will be the message sent: " + messageToSend);
                Thread.sleep(1000);
            }catch(IOException e){
                e.printStackTrace();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }

            forwardMessageOverConnection(Integer.parseInt(messageToSend), messageToSend);
        }
    }
}
