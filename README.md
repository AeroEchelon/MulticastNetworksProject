Multicast Networks Project
==========================

OBJECTIVE
=========

The purpose of this project is to build a network consisting of nodes and links. Once this network is established, it can multicast a message to 'Receiver' nodes that are apart of this multicast group.

This project contains a set of general nodes, links and routing entries between a source node and receiver node. 

DETAILS
=======

Each node contains a set of links where each link contains the information of the 'next node' of which this source node is connected to.

Each link that is estblished creates a TCP socket connection to the 'next hop' nope. Once this 'next hop' node has accepted this socket connection, it check's it's own routing table and forwards this packet to the next node. This process continues until this message has been received at the destination node.


BACKGROUND
==========

An overlay network is a network of communicating devices interconnected through an underlying network that provides transport service between these devices. Popular applications of overlay networks are Peer-to-Peer (P2P) file sharing, such as napster, Kazaa, and application-level multicast etc. In this assignment you will design and implement a Multicast Overlay Network (MON).

Multicast Overlay Network
=========================

A multicast overlay network is a connected graph of nodes and links. Nodes are hosts that are connected to a network, and links are transport-level connections, e.g. TCP or UDP connections, between the two nodes. In a multicast overlay network a set of nodes are deployed as multicast forwarders. A multicast forwarder is a node that is connected to either a receiver or another multicast forwarder. A multicast source node transmits multicast packets to one of more multicast forwarders. A multicast receiver is a node that is interested in receiving the packet. It may be connected to more than one forwarder. For example, node S in Figure 1 is a multicast source while R1, R2 and R3 are receivers. It sends multicast packets to two forwarders, F1 and F2. F2 forwards multicast packets to R2 and R3, whereas F1 forwards the packets to R1.
