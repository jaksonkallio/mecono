# Mecono Documentation

## Terms
* **Neighbor**: A node with hop count 1 (directly connected to self-node) is a neighbor. A connection between two nodes is called a *neighborship*.
* **Ping**: Time, in milliseconds, between a sent ping and a ping response.
* **Self-Node**: The node the host software is running on, with an independent database and functionality.
* **Parcel**: A piece of data.
* **Pallet**: A group of parcels that make up a large piece of data, built on the receiving node.
* **Signal**: A high-level way of referring to a complete pallet made up of parcels, assuming everything is working properly.
* **Remote-Node** or **Node**: A node that isn't a self-node.
* **Community**: Nodes that are within a user-defined hop count are part of community. Communities share paths and announce existence to eachother.
* **Hops**: Distance between two nodes, measured in number of connections traversed. Hop-0 is self node, hop-1 is neighbor, etc.
* **Stop**: A single node in a path.
* **Path**: A path between two nodes, made up of stops.
* **Pinned Node**: A remote node that should always have a known, and tested, path to the self-node to allow for quick communication.
* **Trusted Node**: A remote node that the self node has given "trusted status" to. Path information is requested from trusted nodes. All trusted nodes also automatically act like pinned nodes.
* **Mailbox**: The inbound/outbound coordinator for parcels. Builds pallets and hands them off to the self node when done.
* **Ping (Pallet Type)**: Quick 1-parcel request sent to a node to check online status or latency.
* **Ping Response (Pallet Type)**: Response to a ping.
* **Discover (Pallet Type)**: Request for path information from a specific node.
* **Discover Response (Pallet Type)**: Response to a discover request.
* **Data (Pallet Type)**: Standard parcel that contains a normal data transfer.
* **Data Receipt (Pallet Type)**: After the receiving node fully builds a pallet, it sends a receipt to originator.

## Functions
### Node Birth
The birth of a node is when it is introduced to the network with no knowledge. This could be a fresh install or the user cleared the database. Several things will happen:

1. Generate private/public key.
2. Advertise existence to community.

### Asking for a Path
When a path to a node is unknown, ask some helpful nodes for help.

1. Send discover signal to each community member, with requested node address.
2. Await discover responses.
3. Add each returned path to the node's "known paths" variable.
4. Tell node object to update outdated paths. (Since no paths were ever pinged, all will be updated.)
5. Since paths have a "recommended by" variable to see where the path knowledge came from, we can determine which community members are being honest in the long run.

## UI
### Dashboard
* Online status/ping of community
* Online status/ping of pinned nodes (which includes trusted nodes, trusted nodes listed first)

### Active Pallets
* Lists data parcels awaiting receipt.

### Node Profile
* Relationship setting ("None", "Pinned", "Trusted and Pinned")
* Success rates
* Volume statistics

## Future Incentive Structure
What's stopping a node from responding to ping chunks but not forwarding data? Incentives alleviates a lot of trust issues with the network. It also encourages nodes to pop up and help the network in congested areas, much like how the incentives to mine bitcoin make the network's security increase. It's a way of allowing people to be selfish, yet also vitally help the network at the same time. Incentives are given to forward data chunks in the form of cryptocurrency.

1. A node will only receive a data chunk from another node for two reasons: it is an intermediate hop in path to final destination OR it is the final destination.
2. This means that the path a data chunk must travel to get to destination must be known before sending it out.
3. In the content of the message is a transaction hash that sends cryptocurrency to all hops in the node path. This transaction hash is encrypted with the public key of the final destination, with the final destination being another node that is rewarded. Most likely, this amount will be so incredibly small that it will not be a problem to the SelfNode to pay. In addition, the SelfNode most likely has helped forward messages so, in theory, they'll be earning as much as they're spending.
4. When the message (and therefore the transaction as well) gets to the destination of the path, the destination will decrypt the transaction hash and verify that both they, and the other nodes in the path, will get rewarded.
5. They'll broadcast the message, and the transaction will reward all hops in the path, incentivizing future connnections.
6. If hops in the path don't receive their payment, they might blacklist the connection between the target and the destination and refuse to service the path when these two are at either end. This means that both target and SelfNode are encouraged to play nice and fulfill the operation contract with the hops.
