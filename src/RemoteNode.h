/*
@description A remote node
@author Jakson R. Kallio
*/

#ifndef REMOTENODE_
#define REMOTENODE_

#include <vector>

class RemoteNode {
private:
	std::string privateKey;
	int known_path_count;

	// An array of neighbors this node is connected to
	std::vector<RemoteNode> neighbors;
public:
	std::string getPublicKey() const;
	int getKnownPathCount() const;
};

#endif
