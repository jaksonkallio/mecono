#include <stdlib.h>
#include <time.h>
#include "SimNode.h"

bool SimNode::rnum_seeded = false;

SimNode::SimNode(){
	if(!rnum_seeded){
		srand(time(NULL));
		rnum_seeded = true;
	}
}

unsigned short int SimNode::neighborCount() const{
	return neighbors.size();
}

bool SimNode::hasNeighbor(const SimNode& neighbor) const{
	bool neighborship(false);

	for(unsigned short int i = 0; i < neighborCount(); ++i){
		if(neighbors[i]->getAddress() == neighbor.getAddress()){
			neighborship = true;
		}
	}

	return neighborship;
}

void SimNode::addNeighbor(const SimNode& neighbor){
	neighbors.push_back(neighbor);
}

void SimNode::genAddress(){
	static const char alphanum[] =
	"0123456789"
	"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	"abcdefghijklmnopqrstuvwxyz";
	address = "";

	for (int i = 0; i < address_length; ++i) {
		address += alphanum[rand() % (sizeof(alphanum) - 1)];
	}
}
