#ifndef CHUNK_
#define CHUNK_

#include <vector>
#include <string>

class Chunk {
private:
	// The data in the chunk
	std::string chunk_data;

	// The ID of the chunk in the block
	unsigned int chunk_id;

	// Whether this chunk is a standalone chunk or part of a block
	bool is_block_part;
public:

};
#endif
