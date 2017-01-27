/*
@description A remote node
@author Jakson R. Kallio
*/

#ifndef REMOTENODE_
#define REMOTENODE_

#include <vector>

class SelfNode {
private:
	std::string privateKey;
	int known_path_count;

	// Valid full paths to this node
	std::vector<Path> paths_to;
public:
	std::string getPublicKey() const;
	int getKnownPathCount() const;
};

#endif
