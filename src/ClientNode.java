/**
 * Created by marvinbernal on 2014-03-29.
 */
public class ClientNode {

    /* CONSTANTS */

    private static final int CLIENT_ID = 1;
    private static final int SERVER_ID = 2;


    public static void main (String... args){

        /**
         * 1. Create Nodes
         * 2. Create Links
         * 3. Initialize Links
         * 4. Send message from Source Node.
         * 5. Read message from Receiver Nodes.
         */

        /* READING ARGUMENTS */

        String messageToSend = args[0];


        Node aNode = new Node(CLIENT_ID, Node.Role.SOURCE);

        aNode.initialize();

        aNode.transmitDataToReceiverGivenRID(SERVER_ID, messageToSend);

    }
}
