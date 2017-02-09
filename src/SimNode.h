/*
@description A remote node
@author Jakson R. Kallio
*/

#ifndef SIMNODE_
#define SIMNODE_

#include <vector>

class SimNode: public RemoteNode {
private:
	// If the node will only process ping chunks, but no other kind of chunks
	bool only_process_pings = false;

	// Chance of node to flat out silently ignore chunks
	double chance_ignore = 0.00;

	// Simulated latency in milliseconds
	unsigned int latency = 0;
};
