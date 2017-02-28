#ifndef BLOCK_
#define BLOCK_

#include <vector>
#include <string>
#include "Chunk.h"

class Block {
private:
	// How many chunks are in this block
	unsigned int chunk_count;

	// The store of chunks in the block
	chunk chunk_store[chunk_count];

	// What percentage the block has been constructed
	double construction_percent;

	// Block hash
	std::string block_hash;
public:

};
#endif
