/*
@description A remote node
@author Jakson R. Kallio
*/

#ifndef SIMNODE_
#define SIMNODE_

#include <vector>
#include "RemoteNode.h"
#include "Chunk.h"

class SimNode: public RemoteNode {
private:
	// If the node will only process ping chunks, but no other kind of chunks
	bool only_process_pings = false;

	// Chance of node to flat out silently ignore chunks
	short int ignorance = 10;

	// -- Configuration Variables -- See https://github.com/jaksonkallio/mecono/blob/master/documentation.md#configuration
	unsigned short int community_hop_radius = 3;
	std::string node_label = "My Node";
	unsigned int hop_forward_limit_ping = 30;
	unsigned int hop_forward_limit_chunk = 10;
	unsigned int max_inbound_queue_time = 30000;
	// ^^ END ^^

	// Simulated latency in milliseconds
	unsigned int latency = 0;

	// A list of neighbors this node is connected to
	std::vector<RemoteNode*> neighbors;

	// Known remote nodes
	struct RemoteNodeInfo {
		RemoteNode* the_node;
		bool is_neighbor;
		unsigned int last_latency;
		Path* path_to;
	};
	std::vector<RemoteNodeInfo> known_nodes;
public:
	SimNode();
	bool hasNeighbor(SimNode* neighbor) const;
	unsigned short int neighborCount() const;
	void addNeighbor(SimNode* neighbor);
	Path* getPathToNode(const SimNode& node_target) const;
	void genAddress();
	unsigned int knownNodeCount() const;
	void receiveChunk(Chunk chunk) const;
};

#endif
