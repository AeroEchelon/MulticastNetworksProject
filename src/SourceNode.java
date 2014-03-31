import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by marvinbernal on 2014-03-31.
 *
 * A starting node which multicasts packets from here to other forwarder or receiver nodes.
 */
final class SourceNode extends Node{

    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * IP Address and packet rate are set to default.
     *
     * @param listeningPort Port to listen for incoming connections.
     */
    public SourceNode(int listeningPort) {
        super(1, Role.SOURCE, listeningPort);
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
     * Initialize will initiate a request for user input to be sent to any other node on the network.
     *
     * Because of this, this node should be initialized after the network is stabilized.
     */
    @Override
    public void initialize() {

        System.out.println("Enter something here : ");

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
                    sendMessageGivenRouterID(Integer.parseInt(messageToSend), messageToSend);
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
}
