/*
@description A remote node
@author Jakson R. Kallio
*/

#ifndef REMOTENODE_
#define REMOTENODE_

#include <vector>
#include "Path.h"

class RemoteNode {
private:
	unsigned int known_path_count;

protected:
	static const short int address_length = 5;
	char address[address_length];

public:
	unsigned int getKnownPathCount() const;
	std::string getAddress() const;
};

#endif
