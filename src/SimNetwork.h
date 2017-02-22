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
	std::vector<SimNode*> all_nodes;

	// Row/columns of simulated network for proximity and also determines total count of nodes
	const unsigned int rows = 5;
	const unsigned int columns = 5;
	const unsigned int node_count = rows * columns;

	static const unsigned short int neighbor_connectivity = 50;

	unsigned int neighbor_connections = 0;
public:
	// Constructor
	SimNetwork();

	// Draw the network grid graphically on the console
	void drawNetworkGrid() const;

	// Get the count of nodes being tracked
	unsigned int nodeCount() const;

	// Check if two nodes are neighbors
	bool isNeighbor(unsigned int node_id_a, unsigned int node_id_b) const;

	// Sum of all bytes transferred over the network
	unsigned int sumBytesTransferred() const;

	// Count of neighbor connections
	unsigned int neighborConnectionCount() const;

	// Given a row and column, returns the array ID of the node.
	unsigned int rcToIth(unsigned int row, unsigned int column) const;

	// List all nodes
	void listNodes() const;

	// Generate neighbor connections
	void genNeighborship();
};

#endif
