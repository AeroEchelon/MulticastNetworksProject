/**
 * Created by marvinbernal on 2014-03-29.
 */
public class ServerNode {

    /* CONSTANTS */

    private static final int CLIENT_ID = 1;
    private static final int SERVER_ID = 2;


    public static void main (String... arg){

        /**
         * 1. Create Nodes
         * 2. Create Links
         * 3. Initialize Links
         * 4. Send message from Source Node.
         * 5. Read message from Receiver Nodes.
         */

        /* READING ARGUMENTS */

        int port = Integer.parseInt(arg[0]);

        Node aNode = new Node(SERVER_ID, Node.Role.SOURCE);
        Node bNode = new Node(CLIENT_ID, Node.Role.RECEIVER);

        aNode.addDestinationNode(bNode, port);
        bNode.addSourceNode(aNode, port);

        aNode.initialize();
        bNode.initialize();

        aNode.transmitDataToReceiverGivenRID(bNode.getRouterID(), "Hello.");

    }
}
