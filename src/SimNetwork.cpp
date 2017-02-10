#include <iostream>

#include "SimNetwork.h"

void SimNetwork::drawNetworkGrid() const{
	unsigned short int symbol_row[columns];
	unsigned int column = i % columns;
	unsigned int row = (int)(i / columns);
	unsigned short int row_type = 0;

	for(unsigned int i = 0; i < nodeCount(); i++){
		column = i % columns;
		row = (int)(i / columns);

		if(row_type % 2 == 0){
			// Row of nodes
		}else{
			// Symbol row
		}

		++row_type;
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
