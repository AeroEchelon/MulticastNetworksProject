/**
 * Created by marvinbernal on 2014-03-29.
 *
 * A simple data structure containing next hop nodes, destination node and link (interface).
 *
 */
final class RoutingEntry {

    private Node mNextHopNode;
    private Node mDestinationNode;
    private Link mLink;

    /**
     * Constructs a Routing entry to a destination node, given a next hop node and link interface to use to connect
     * to the next hop node.
     *
     * @param destinationNode
     * @param link
     */
    public RoutingEntry(Link link, Node destinationNode) {
        mNextHopNode = link.getDestinationNode();
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

    public void setLink(Link link) {
        mLink = link;
    }

    public void setNextHopNode(Node node){
        mNextHopNode = node;
    }
}
