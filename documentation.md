# Mecono Documentation

## Classes
* SelfNode: The node representing the single host node on a machine, or in a simulated environment, each simulated node.
* Node: A remote node in the network.
* DataChunk: A chunk of data that can be sent through the network.

## Terms
* **Neighbor**: A node with hop count 1 (directly connected to self-node) is a neighbor.
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

## Configuration
* `community_hop_radius: Integer`: What number of hops is considered a "community".
* `node_label: String`: Customizable, unregulated, string that represents the self-node in human readable form.
* `int hop_forward_limit_ping: Integer`: Don't forward ping chunks that have travelled more than X hops.
* `int hop_forward_limit_data: Integer`: Don't forward data chunks that have travelled more than X hops.
* `max_inbound_queue_time: Integer`: Max time, in milliseconds, to keep a chunk in the inbound queue.

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

## Data Structures
### Paths
