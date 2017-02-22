/*
@description A remote node
@author Jakson R. Kallio
*/

#ifndef SIMNODE_
#define SIMNODE_

#include <vector>
#include "RemoteNode.h"

class SimNode: public RemoteNode {
private:
	// If the node will only process ping chunks, but no other kind of chunks
	bool only_process_pings = false;

	// Chance of node to flat out silently ignore chunks
	short int ignorance = 10;

	// Simulated latency in milliseconds
	unsigned int latency = 0;

	// A list of neighbors this node is connected to
	std::vector<RemoteNode*> neighbors;

public:
	SimNode();
	bool hasNeighbor(SimNode* neighbor) const;
	unsigned short int neighborCount() const;
	void addNeighbor(SimNode* neighbor);
	void genAddress();
};

#endif
