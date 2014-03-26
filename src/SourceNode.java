import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by marvinbernal on 2014-03-26.
 *
 * This reciever node is also known as a 'First Hop Forwarder'.
 *
 */
public class SourceNode extends Node {

    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param role
     * @param IPAddress
     * @param receivingPacketRate
     */
    public SourceNode(int routerID, int role, String IPAddress, int receivingPacketRate) throws UnknownHostException {
        super(routerID, role, IPAddress, receivingPacketRate);
    }

    public void forwardData(){

    }

}
