#include <iostream>

#include "SimNetwork.h"

void SimNetwork::drawNetworkGrid() const{
	for(unsigned int i = 0; i < nodeCount(); i++){
		unsigned int column = i % columns;
		unsigned int row = (int)(i / columns);
	}
}

unsigned int SimNetwork::nodeCount() const{
	return node_count;
}

bool SimNetwork::isNeighbor(unsigned int node_id, unsigned short int port) const{
	assert(port >= 0 && port <= 3);

	return true;
}

unsigned int SimNetwork::sumBytesTransferred() const{
	return 0;
}
