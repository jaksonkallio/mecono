# Mecono Documentation

## Classes
* SelfNode: The node representing the single host node on a machine, or in a simulated environment, each simulated node.
* Node: A remote node in the network.
* DataChunk: A chunk of data that can be sent through the network.

## Terms
* **Neighbor**: A node with hop count 1... directly connected to self-node.
* **Self-Node**: The node the host software is running on, with an independent database and functionality.
* **Remote-Node** or **Node**: A node that isn't a self-node.

## Functions
### Node Birth
The birth of a node is when it is introduced to the network with no knowledge. This could be a fresh install or the user cleared the database. Several things will happen:
1. Generate private/public key.
2. Advertise existence to all neighbors.
