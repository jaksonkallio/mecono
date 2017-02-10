#include <iostream>
#include <assert.h>

#include "SimNetwork.h"

void SimNetwork::drawNetworkGrid() const{
	unsigned short int symbol_row[columns];

	for(unsigned int r = 0; r < ((rows * 2) - 1); r++){
		if(r % 2 == 0){
			// Row of nodes
			for(unsigned int c = 0; c < ((columns * 2) - 1); c++){
				// Each vertical line
				if(c % 2 == 0){
					std::cout << "O";
				}else{
					std::cout << " ";
				}
			}
		}else{
			// Symbol row
		}

		std::cout << "\n";
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
