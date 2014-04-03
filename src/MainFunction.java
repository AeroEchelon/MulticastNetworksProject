/**
 * Created by marvinbernal on 2014-03-29.
 *
 * This class is a main class that provides a demonstration of the network.
 *
 * The basic network topology is as outlined as below:
 *
 *              S[1]
 *           /        \
 *          F[2]      F[3]
 *           \        /
 *              R[4]
 *
 * In the above topology, the source node contains routing entries to each node in the network.
 * F[3] containsa routing entry to R[4], while F[2] does not. It is important to note that F[2] does contain
 * a link to R[4] however, when it receves a packet it does not contain a routing entry to allow this route to R[4].
 *
 * You can view the networy topology in the source code below.
 *
 */
public class MainFunction {

    public static void main (String... arg) throws InterruptedException{

        /**
         * 1. Create Nodes
         * 2. Add nodes to respective
         * 4. Send message from Source Node.
         * 5. Read message from Receiver Nodes.
         */

        /* READING ARGUMENTS */

        int aListeningPort = 6066;
        int bListeningPort = 6061;
        int cListeningPort = 6062;
        int dListeningPort = 6063;

        System.out.println("Creating Nodes ...");

        // This is using Node's lazy constructor. IP address for all notes is assumed to be 'localhost'.
        Node aNode = new SourceNode(1, aListeningPort);
        Node bNode = new ForwarderNode(2, bListeningPort);
        Node cNode = new ForwarderNode(3, cListeningPort);
        Node dNode = new ReceiverNode(4, dListeningPort);

        System.out.println("\nEstablishing physical links ...");
        aNode.addDestinationNode(bNode);
        aNode.addDestinationNode(cNode);

        bNode.addDestinationNode(dNode);
        cNode.addDestinationNode(dNode);

        System.out.println("\nAdding routing entries to each node ...");
        aNode.addRoutingEntry(aNode, aNode);
        aNode.addRoutingEntry(bNode, bNode);
        aNode.addRoutingEntry(cNode, cNode);
        aNode.addRoutingEntry(bNode, dNode);

        bNode.addRoutingEntry(bNode,bNode);
        bNode.addRoutingEntry(dNode,dNode);

        cNode.addRoutingEntry(cNode,cNode);
        cNode.addRoutingEntry(dNode,dNode);

        System.out.println("\nInitializing nodes ...");
        bNode.initialize();
        cNode.initialize();
        dNode.initialize();

        Thread.sleep(200);

        System.out.println("\nStart!");
        aNode.initialize();
    }
}
