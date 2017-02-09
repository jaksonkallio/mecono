/*
@description A remote node
@author Jakson R. Kallio
*/

#ifndef SIMNETWORK_
#define SIMNETWORK_

#include <vector>

class SimNetwork {
private:
	// All of the nodes in the network
	std::vector<SimNode> all_nodes;

public:
	void drawNetworkGrid() const;
};

#endif
