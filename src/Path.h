/*
@description Represents a path (set of path segments to a specific node)
@author Jakson R. Kallio
*/

#ifndef PATH_
#define PATH_

#include <vector>

class Path {
private:
	// List of PathSegment pointers
	std::vector<PathSegment> path;
public:
	// Get hash of the collection of path segments
	std::string getHash() const;
};

#endif
