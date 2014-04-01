import java.util.ArrayList;

/**
 * Created by marvinbernal on 2014-03-29.
 */
public class MainFunction {

    public static void main (String... arg) throws InterruptedException{

        /**
         * This main requires the following stages after parsing configruation file
         *
         * 1. Creating main node and neighbouring nodes.
         * 2. Adding neighbouring nodes to main node.
         */

        /* READING ARGUMENTS */

        // default group IDs
        ArrayList<Integer> groupIDs = new ArrayList<Integer>();
        groupIDs.add(1);

        // read file
        int aListeningPort = 6066;
        int bListeningPort = 6061;
        int cListeningPort = 6062;
        int dListeningPort = 6063;

        System.out.println("Creating Nodes ...");
        // This is using Node's lazy constructor. IP address for all notes is assumed to be 'localhost'.
        Node aNode = new ForwarderNode(1, aListeningPort);

        Node bNode = new ReceiverNode(2, bListeningPort, groupIDs);
        Node cNode = new ReceiverNode(3, cListeningPort, groupIDs);
        Node dNode = new ReceiverNode(4, dListeningPort, groupIDs);

        System.out.println("\nEstablishing physical links ...");
        aNode.addDestinationNode(bNode);
        aNode.addDestinationNode(cNode);
        aNode.addDestinationNode(dNode);


        System.out.println("\nInitializing nodes ...");

    }
}
