#include <stdlib.h>
#include <time.h>
#include "SimNode.h"

unsigned short int SimNode::neighborCount() const{
	return neighbors.size();
}

bool SimNode::hasNeighbor(SimNode* neighbor) const{
	bool neighborship(false);

	for(unsigned short int i = 0; i < neighborCount(); ++i){
		if(neighbors[i]->getAddress() == neighbor->getAddress()){
			neighborship = true;
		}
	}

	return neighborship;
}

void SimNode::addNeighbor(SimNode* neighbor){
	neighbors.push_back(neighbor);
}

void SimNode::genAddress(){
	static const char alphanum[] =
	"0123456789"
	"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	"abcdefghijklmnopqrstuvwxyz";

	srand(time(NULL));

	for (int i = 0; i < address_length; ++i) {
		address[i] = alphanum[rand() % (sizeof(alphanum) - 1)];
	}

	address[address_length] = 0;
}
