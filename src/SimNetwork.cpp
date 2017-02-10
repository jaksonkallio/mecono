#include <iostream>
#include <assert.h>

#include "SimNetwork.h"

void SimNetwork::drawNetworkGrid() const{
	char symbol_row[columns];

	for(unsigned int cai = 0; cai < ((columns * 2) - 1); cai++){
		symbol_row[cai] = ' ';
	}

	for(unsigned int r = 0; r < ((rows * 2) - 1); r++){
		if(r % 2 == 0){
			// Row of nodes
			for(unsigned int c = 0; c < columns; c++){
				// Each vertical line
				std::cout << "#";

				if(isNeighbor(rcToIth(r, c), rcToIth(r, c + 1)) && c != (columns - 1)){
					std::cout << "-";
				}else{
					std::cout << " ";
				}

				if(isNeighbor(rcToIth(r, c), rcToIth(r + 1, c))){
					symbol_row[c * 2] = '|';
				}

				if(isNeighbor(rcToIth(r, c), rcToIth(r + 1, c + 1)) && isNeighbor(rcToIth(r, c + 1), rcToIth(r + 1, c))){
					symbol_row[c * 2] = '|';
				}
			}
		}else{
			for(unsigned int ca = 0; ca < ((columns * 2) - 1); ca++){
				std::cout << symbol_row[ca];
				symbol_row[ca] = ' ';
			}
		}

		std::cout << "\n";
	}
}

unsigned int SimNetwork::rcToIth(unsigned int row, unsigned int column) const{
	return (row * columns) + column;
}

unsigned int SimNetwork::nodeCount() const{
	return node_count;
}

bool SimNetwork::isNeighbor(unsigned int node_id_a, unsigned int node_id_b) const{
	return true;
}

unsigned int SimNetwork::sumBytesTransferred() const{
	return 0;
}
