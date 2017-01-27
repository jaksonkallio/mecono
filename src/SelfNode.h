/*
@description The object representing the self-node
@author Jakson R. Kallio
*/

#ifndef SELFNODE_
#define SELFNODE_

#include <vector>

class SelfNode {
private:
	std::string privateKey;
	int known_path_count;
	
public:
	std::string getPublicKey() const;
	int getKnownPathCount() const;
};

#endif
