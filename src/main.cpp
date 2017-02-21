/*
@description Program driver file
@author Jakson R. Kallio
*/

#include <iostream>

#include "SimNetwork.h"
#include "SimNode.h"

int main(){
	SimNetwork* sim = new SimNetwork();
	sim->genNeighborship();
	sim->drawNetworkGrid();
	//sim->listNodes();

	/*
	SimNode* testnode = new SimNode();
	testnode->genAddress();
	std::cout << testnode->getAddress() << "\n";
	*/

	return EXIT_SUCCESS;
}
