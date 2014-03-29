/**
 * Created by marvinbernal on 2014-03-29.
 *
 * A simple data structure containing next hop nodes, destination node and link (interface).
 */
public class RoutingEntry {

    private Node mNextHopNode;
    private Node mDestinationNode;
    private Link mLink;

    public RoutingEntry(Node nextHopNode, Node destinationNode, Link link) {
        mNextHopNode = nextHopNode;
        mDestinationNode = destinationNode;
        mLink = link;
    }

    public Node getNextHopNode() {
        return mNextHopNode;
    }

    public Node getDestinationNode() {
        return mDestinationNode;
    }

    public Link getLink() {
        return mLink;
    }
}
