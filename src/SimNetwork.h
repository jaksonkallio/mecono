/*
@description The entire simulated network
@author Jakson R. Kallio
*/

#ifndef SIMNETWORK_
#define SIMNETWORK_

#include <vector>

#include "SimNode.h"

class SimNetwork {
private:
	// All of the nodes in the network
	std::vector<SimNode> all_nodes;

	// Row/columns of simulated network for proximity and also determines total count of nodes
	const unsigned int rows = 5;
	const unsigned int columns = 5;
	const unsigned int node_count = rows * columns;

public:
	void drawNetworkGrid() const;
	unsigned int nodeCount() const;
	bool isNeighbor(unsigned int node_id_a, unsigned int node_id_b) const;
	unsigned int sumBytesTransferred() const;

	// Given a row and column, returns the array ID of the node.
	unsigned int rcToIth(unsigned int row, unsigned int column) const;
};

#endif
