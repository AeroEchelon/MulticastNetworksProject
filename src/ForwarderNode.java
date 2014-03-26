import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by marvinbernal on 2014-03-26.
 *
 * This reciever node is also known as a 'Controller'.
 *
 */
public class ForwarderNode extends Node{

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param role
     * @param IPAddress
     * @param receivingPacketRate
     */
    public ForwarderNode(int routerID, int role, String IPAddress, int receivingPacketRate) throws UnknownHostException {
        super(routerID, role, IPAddress, receivingPacketRate);
    }

    public void forwardData(){
        // TODO Implement

    }

    public void receivedData(){
        // TODO Implement
    }

}