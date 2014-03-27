import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by marvinbernal on 2014-03-26.
 *
 * This reciever node is also known as a 'Leaf Forwarder'.
 *
 */
public class ReceiverNode extends Node {

    private ServerSocket    serverSocket;
    private Socket          server;

    /**
     * Primary node constructor.
     *
     * This node is created will only accept incoming data and print each packet to standard output.
     *
     * @param routerID
     * @param role
     * @param IPAddress
     * @param receivingPacketRate
     */
    public ReceiverNode(int routerID, int role, String IPAddress, int receivingPacketRate) throws IOException {
        super(routerID, role, IPAddress, receivingPacketRate);

        serverSocket = new ServerSocket(GENERIC_PORT);
        serverSocket.setSoTimeout(SOCKET_TIMEOUT);
        System.out.println("Server has connected.");

        System.out.println("Listening on port " + serverSocket.getLocalPort());
        server =  serverSocket.accept(); // blocks until a connection is made

        System.out.println("Connected to " + server.getRemoteSocketAddress());
    }

    /**
     * Starts the node. This method is the last method that should be run after all initialization is setup.
     *
     * @throws IOException
     */
    @Override
    void initialize() throws IOException {
        while(true){
            receiveData();
        }
    }

    /**
     * Prints data incoming from the input stream specified on the listening port.
     */
    public void receiveData(){
        try{
            DataInputStream dataInputStream = new DataInputStream(server.getInputStream());
            System.out.println(dataInputStream.readUTF());
        }catch (SocketTimeoutException e){
            System.out.println("Socket timed out!");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
