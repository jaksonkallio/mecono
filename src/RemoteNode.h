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
	std::string address;
	unsigned int known_path_count;

public:
	std::string getPublicKey() const;
	unsigned int getKnownPathCount() const;
	std::string getAddress() const;
};

#endif
