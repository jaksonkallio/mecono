unsigned short int SimNode::neighborCount() const{
	return neighbors.size();
}

bool SimNode::hasNeighbor(SimNode* neighbor) const{
	bool neighborship(false);

	for(unsigned short int i = 0; i < neighborCount(); ++i){
		if(neighbors[i] == neighbor->getAddress()){
			neighborship = true;
		}
	}

	return neighborship;
}

void SimNode::addNeighbor(SimNode* neighbor){
	neighbors.push_back(neighbor);
}
