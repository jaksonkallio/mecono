#include <iostream>

#include "SimNetwork.h"

void SimNetwork::drawNetworkGrid() const{
	unsigned short int symbol_row[columns];
	unsigned int column = 0;
	unsigned int row = 0;
	unsigned short int row_type = 0;

	for(unsigned int i = 0; i < nodeCount(); i++){
		column = i % columns;
		if((int)(i / columns) != row){
			++row_type;
			row = (int)(i / columns);
		}

		if(row_type % 2 == 0){
			// Row of nodes
			for(unsigned int q = 0; q < ((columns * 2) - 1)); q++){
				// Each vertical line
				if(q % 2 == 0){
					std::cout << "O";
				}else{
					std::cout << " ";
				}
			}
		}else{
			// Symbol row
		}
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
