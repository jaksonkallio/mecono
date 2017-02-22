#include <stdlib.h>
#include <time.h>
#include <vector>
#include "SimNode.h"

SimNode::SimNode(){
}

unsigned short int SimNode::neighborCount() const{
	unsigned int neighbor_count(0);
	unsigned int i(0);

	while(i < knownNodeCount()){
		if(known_nodes[i].is_neighbor){
			++neighbor_count;
		}

		++i;
	}

	return neighbor_count;
}

bool SimNode::hasNeighbor(SimNode* neighbor) const{
	bool neighborship(false);
	unsigned int i(0);

	while(i < knownNodeCount() && !neighborship){
		if(known_nodes[i].the_node->getAddress() == neighbor->getAddress()){
			neighborship = true;
		}

		++i;
	}

	return neighborship;
}

void SimNode::addNeighbor(SimNode* neighbor){
	Path* new_path = new Path();
	RemoteNodeInfo new_neighbor {
		neighbor,
		true,
		0,
		new_path
	};
	known_nodes.push_back(new_neighbor);
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

Path* SimNode::getPathToNode(const SimNode& node_target) const{
	Path* path_to_target = nullptr;
	int i(0);
	bool path_found(false);

	while(i < knownNodeCount() && !path_found){
		if(known_nodes[i].the_node->getAddress() == node_target.getAddress()){
			path_found = true;
			path_to_target = known_nodes[i].path_to;
		}

		++i;
	}

	return path_to_target;
}

unsigned int SimNode::knownNodeCount() const{
	return known_nodes.size();
}
