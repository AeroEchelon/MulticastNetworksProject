import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Created by marvinbernal on 2014-03-31.
 *
 * A starting node which multicasts packets from here to other forwarder or receiver nodes.
 */
final class SourceNode extends Node{

    Link currentImmediateLink;

    public static final String REQUEST_TO_FORWARDER = "S";

    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * IP Address and packet rate are set to default.
     *
     * @param listeningPort Port to listen for incoming connections.
     */
    public SourceNode(double routerID, int listeningPort) {
        super(routerID, Role.SOURCE, listeningPort);
    }

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param stringAddressOfNode
     * @param listeningPort
     * @param receivingPacketRate
     */
    public SourceNode(double routerID, String stringAddressOfNode, int listeningPort, int receivingPacketRate) {
        super(routerID, Role.SOURCE, stringAddressOfNode, listeningPort, receivingPacketRate);
    }

    /**
     * This is a recursive function that will add routing entries to all nodes in the entire network as destination nodes
     *
     * @param nextHopNode
     */
    private void addRoutingEntryForEveryNodeBeyondNextHopNode(Node nextHopNode){

        Iterator<Link> linkIterator = getLinks().iterator();

        while(linkIterator.hasNext()){

            while(!nextHopNode.getLinks().isEmpty()){
                addRoutingEntryForEveryNodeBeyondNextHopNode(nextHopNode);
            }

            this.addRoutingEntry(currentImmediateLink.getDestinationNode(), nextHopNode);
        }
    }

    /**
     * Initialize will initiate a request for user input to be sent to any other node on the network.
     *
     * Because of this, this node should be initialized after the network is stabilized.
     */
    @Override
    public void initialize() {

        System.out.println("Enter string with the following syntax:\nRECEIVER_NODE_ID, MESSAGE");

        // Iterate through all links and add each link to routing table. This is possible because this is the source node.
        addRoutingEntryForEveryNodeBeyondNextHopNode(this);

        while(true){

            String messageToSend;
            try{
                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                messageToSend = bufferRead.readLine();

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
                    sendPacket(Integer.parseInt(messageToSend), messageToSend);
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

    /**
     * Sends all link information to destination node.
     */
    @Override
    public void configureRoutingTable() {
        /**
         * This source will iterate through all of its links and add routing entries to each forwarder node.
         */

        Iterator<Link> linkIterator = getLinks().iterator();

        while(linkIterator.hasNext()){
            Link link = linkIterator.next();

            addRoutingEntry(link.getDestinationNode(), link.getDestinationNode());

            link.getDestinationNode();

        }
    }
}
