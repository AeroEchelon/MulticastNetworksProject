import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Created by marvinbernal on 2014-03-31.
 *
 * A starting node which multicasts packets from here to other forwarder or receiver nodes.
 */
final class SourceNode extends Node{

     /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * @param routerID
     * @param listeningPort
     */
    public SourceNode(int routerID, int listeningPort) {
        super(routerID, Role.SOURCE, listeningPort);
    }

    /**
     * Primary source node constructor.
     *
     * @param nodeID                The ID of the node.
     * @param IPAddress             The IPAddress of the node.
     * @param listeningPort         The listening port used to listen for incoming connections.
     * @param receivingPacketRate   The packet rate at which this node can recieving incoming packets.
     */
    public SourceNode(int nodeID, Role role, String IPAddress, int listeningPort, int receivingPacketRate) {
        super(nodeID, role, IPAddress, listeningPort, receivingPacketRate);
    }

    /**
     * Initialize will initiate a request for user input to be sent to any other node on the network.
     *
     * Because of this, this node should be initialized after the network is stabilized.
     */
    @Override
    public void initialize() {

        System.out.println("Please construct a packet <destinationID, message> that will traverse through the network to the receiver.");
        System.out.println("For the purposes of the topology outlined below, the receiver ID is \"4\"");

            String messageToSend;
            String receiverID;
            try{
                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter the ID of the node you wish this packet to transfer to: ");
                receiverID = bufferRead.readLine();
                System.out.print("Enter Message to send to receiver: ");
                messageToSend = bufferRead.readLine();

                messageToSend = receiverID + ", " + messageToSend;

                System.out.println("This will be the message sent: " + messageToSend);
                Thread.sleep(1000);

                if (messageToSend.equals("")){
                    System.out.println("Please enter valid input.");
                }else if(messageToSend.equals("EXIT")){
                    System.out.println("Goodbye!");
                    getServerSocket().close();
                    getSocket().close();
                    System.exit(0);
                }else{

                    StringTokenizer tokenPacket = new StringTokenizer(messageToSend,", ");

                    String packetDestination = tokenPacket.nextToken();
                    String message = tokenPacket.nextToken();

                    sendMessageGivenRouterID(Integer.parseInt(packetDestination), messageToSend);
                }


            }catch(IOException e){
                e.printStackTrace();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
            catch (Exception e){

                System.out.println("Something wrong happened. Terminating process.");
                System.out.println("Goodbye!");
                System.exit(0);
            }

    }
}
