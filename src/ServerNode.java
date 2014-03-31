import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by marvinbernal on 2014-03-29.
 */
public class ServerNode {

    /* CONSTANTS */

    private static final int CLIENT_ID = 1;
    private static final int SERVER_ID = 2;


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

        System.out.println("Creating Network ...");
        aNode.addDestinationNode(bNode);
        aNode.addDestinationNode(cNode);

        bNode.addDestinationNode(dNode);
        cNode.addDestinationNode(dNode);

        System.out.println("Adding routing entries to Nodes");
        aNode.addRoutingEntry(aNode, aNode);
        aNode.addRoutingEntry(bNode, bNode);
        aNode.addRoutingEntry(cNode, cNode);
        aNode.addRoutingEntry(bNode, dNode);

        bNode.addRoutingEntry(bNode,bNode);
        bNode.addRoutingEntry(dNode,dNode);

        cNode.addRoutingEntry(cNode,cNode);
        cNode.addRoutingEntry(dNode,dNode);

        bNode.initialize();
        cNode.initialize();
        dNode.initialize();

        Thread.sleep(200);

        aNode.initialize();


    }
}
