# Multicast Networks Project

This is still a work in progress. If you'd like to check out a demonstration of this network, checkout the most recent [release](https://github.com/AeroEchelon/MulticastNetworksProject/releases), compile each class and run the psmv function.

To be explicit,

1. First download the repository either via git clone or downloading as a .zip.
2. Open the project in Terminal and compile each class by entering:
<pre>javac *.java</pre>
3. To start this application, type:
4. <pre> java ServerNode </pre>
5. Finally, enter the ID of the Node that you wish to receive this packet and watch the message transfer! This message will start at the Source node and traverse through each node until it reaches it's destination node.


### OBJECTIVE

The purpose of this project is to build a network that is capable of sending data or 'application layer packet' from one machine to another. These networked machines or 'nodes' are independant of each other in regards to traffic routing. When a 'packet' is received, it checks the header of this packet, checks it's corresponding routing table to see if it has a entry for the destination of this packet. If there is a match, the routing entry has the information to the next machine. This process repeats until this packet has reached its destination.

### IMPLEMENTATION

Each machine is represented as a 'node' and each node can connect to other nodes through the concept of a 'link'. Each link is essentially a high level Socket that also contains the two nodes connecting one node to another (source / destination) as well as the link cost to use this link.

Each link that is estblished creates a TCP socket connection to the 'next hop' nope. Once this 'next hop' node has accepted this socket connection, it check's it's own routing table and forwards this packet to the next node. This process continues until this message has been received at the destination node.


## MOTIVATION

An overlay network is a network of communicating devices interconnected through an underlying network that provides transport service between these devices. Popular applications of overlay networks are Peer-to-Peer (P2P) file sharing, such as napster, Kazaa, and application-level multicast etc. In this assignment you will design and implement a networks that is capable of Multicasting.

### Multicast Overlay Network

A multicast overlay network is a connected graph of nodes and links. Nodes are hosts that are connected to a network, and links are transport-level connections, e.g. TCP or UDP connections, between the two nodes. In a multicast overlay network a set of nodes are deployed as multicast forwarders. A multicast forwarder is a node that is connected to either a receiver or another multicast forwarder. A multicast source node transmits multicast packets to one of more multicast forwarders. A multicast receiver is a node that is interested in receiving the packet. It may be connected to more than one forwarder. For example, node S in Figure 1 is a multicast source while R1, R2 and R3 are receivers. It sends multicast packets to two forwarders, F1 and F2. F2 forwards multicast packets to R2 and R3, whereas F1 forwards the packets to R1.
