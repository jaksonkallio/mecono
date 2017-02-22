#include <iostream>
#include <assert.h>
#include <vector>

#include "SimNetwork.h"

SimNetwork::SimNetwork() {
	for(unsigned int i = 0; i < node_count; ++i){
		SimNode* new_node = new SimNode();
		new_node->genAddress();
		all_nodes.push_back(new_node);
	}
}

unsigned int SimNetwork::neighborConnectionCount() const{
	return neighbor_connections;
}

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
				std::cout << "o";

				if(isNeighbor(rcToIth(r / 2, c), rcToIth(r / 2, c + 1)) && c != (columns - 1)){
					std::cout << "-";
				}else{
					std::cout << " ";
				}

				if(isNeighbor(rcToIth(r / 2, c), rcToIth((r / 2) + 1, c))){
					symbol_row[c * 2] = '|';
				}

				if(isNeighbor(rcToIth(r / 2, c), rcToIth((r / 2) + 1, c + 1))){
					if(isNeighbor(rcToIth(r / 2, c + 1), rcToIth((r / 2) + 1, c))){
						symbol_row[(c * 2) + 1] = 'X';
					}else{
						symbol_row[(c * 2) + 1] = '\\';
					}
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
	if(node_id_a >= node_count || node_id_b >= node_count){
		return false;
	}else{
		return all_nodes[node_id_a]->hasNeighbor(all_nodes[node_id_b]);
	}
}

unsigned int SimNetwork::sumBytesTransferred() const{
	return 0;
}

void SimNetwork::listNodes() const{
	for(unsigned int i = 0; i < node_count; ++i){
		std::cout << "Node #" << i << " - " << all_nodes[i]->getAddress() << "\n";
	}
}

void SimNetwork::genNeighborship(){
	for(unsigned int i = 0; i < node_count; ++i){
		for(unsigned short int j = 0; j <= 3; ++j){
			if((rand() % 101) <= neighbor_connectivity){
				switch(j){
					case 0:
						// Hori, right
						if(((i + 1) % columns > 0) && (i < (node_count - 1))){
							++neighbor_connections;
							all_nodes[i]->addNeighbor(all_nodes[i + 1]);
						}
						break;

					case 1:
						// Diag, bottom right
						if(((i + 1) % columns > 0) && ((i + columns) < (node_count - 1))){
							++neighbor_connections;
							all_nodes[i]->addNeighbor(all_nodes[i + columns + 1]);
						}
						break;

					case 2:
						// Vert, bottom
						if((i + columns) <= (node_count - 1)){
							++neighbor_connections;
							all_nodes[i]->addNeighbor(all_nodes[i + columns]);
						}
						break;

					case 3:
						// Diag, bottom left
						break;
				}
			}
		}
	}

	std::cout << neighbor_connections << " neighbor connections in the network.\n";
}
