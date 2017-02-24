# Mecono Documentation

## Terms
* **Neighbor**: A node with hop count 1 (directly connected to self-node) is a neighbor. A connection between two nodes is called a *neighborship*.
* **Ping**: Time, in milliseconds, between a sent ping chunk and a ping response.
* **Self-Node**: The node the host software is running on, with an independent database and functionality.
* **Chunk**: A piece of data. Can be either a "ping", "data", or "receipt" type.
* **Receipt Chunk**: A response that a node received a specific chunk.
* **Remote-Node** or **Node**: A node that isn't a self-node.
* **Community**: Nodes that are within a user-defined hop count are part of community. Larger values make for more local stability, but O(n^2) ping operations. Communities are used to orient nodes when they come online and to allow for a larger net to catch inbound connections.
* **Hop**: Distance between two nodes, measured in number of nodes between the two plus one.
* **Path Segment**: A connection between two nodes that has a unique ID that is the hash of the two node addresses concatenated.
* **Path**: A path between two nodes, made up of path segment objects.
* **Pinned Node**: A remote node that should always have a known, and tested, path to the self-node to allow for quick communication. More pinned nodes means more load to test paths constantly.
* **Inbound Queue**: A thread that checks for new inbound chunks and holds them in a FIFO queue for processing/forwarding.

## Classes
* `SelfNode`: The node representing the single host node on a machine, or in a simulated environment, each simulated node.
* `RemoteNode`: A remote node that is not the self-node.
* `Chunk`: A chunk of data that can be sent through the network.
* `Path`: A collection of path segments with various statistics about each segment.
* `PathSegment`: A connection between two nodes.

## Configuration
### General
* `community_hop_radius: Integer`: What number of hops is considered a "community".
* `node_label: String`: Customizable, unregulated, string that represents the self-node in human readable form.
* `int hop_forward_limit_ping: Integer`: Don't forward ping chunks that have travelled more than X hops.
* `int hop_forward_limit_data: Integer`: Don't forward data chunks that have travelled more than X hops.
* `max_inbound_queue_time: Integer`: Max time, in milliseconds, to keep a chunk in the inbound queue.

### `SimNetwork.h` Simulation Variables
* `neighbor_connectivity: Integer`: The percent chance for a neighborship between adjacent nodes to be generated.
* `rows: Integer`: Number of rows to be created in the simulated grid.
* `columns: Integer`: Number of columns to be created in the simulated grid.

## Functions
### Node Birth
The birth of a node is when it is introduced to the network with no knowledge. This could be a fresh install or the user cleared the database. Several things will happen:

1. Generate private/public key.
2. Advertise existence to community.
3. Go about normal "Going Online" procedure.

### Going Online/Refreshing Community
When a self-node goes online, it must do some things to reorient itself into the network.

1. Ping nodes in the community by sending out a ping chunk with a `die_after` set to `community_hop_radius`.
2. Test paths to pinned nodes.

### Incentive Structure
What's stopping a node from responding to ping chunks but not forwarding data? Incentives alleviates a lot of trust issues with the network. It also encourages nodes to pop up and help the network in congested areas, much like how the incentives to mine bitcoin make the network's security increase. It's a way of allowing people to be selfish, yet also vitally help the network at the same time. Incentives are given to forward data chunks in the form of cryptocurrency.

1. A node will only receive a data chunk from another node for two reasons: it is an intermediate hop in path to final destination OR it is the final destination.
2. This means that the path a data chunk must travel to get to destination must be known before sending it out.
3. In the content of the message is a transaction hash that sends cryptocurrency to all hops in the node path. This transaction hash is encrypted with the public key of the final destination, with the final destination being another node that is rewarded. Most likely, this amount will be so incredibly small that it will not be a problem to the SelfNode to pay. In addition, the SelfNode most likely has helped forward messages so, in theory, they'll be earning as much as they're spending.
4. When the message (and therefore the transaction as well) gets to the destination of the path, the destination will decrypt the transaction hash and verify that both they, and the other nodes in the path, will get rewarded.
5. They'll broadcast the message, and the transaction will reward all hops in the path, incentivizing future connnections.
6. If hops in the path don't receive their payment, they might blacklist the connection between the target and the destination and refuse to service the path when these two are at either end. This means that both target and SelfNode are encouraged to play nice and fulfill the operation contract with the hops.
