/*
@description The object representing the self-node
@author Jakson R. Kallio
*/

#ifndef SELFNODE_
#define SELFNODE_

#include <vector>

class SelfNode {
private:
  // Private key credentials	
  std::string privateKey;
  
  // -- Configuration Variables -- See https://github.com/jaksonkallio/mecono/blob/master/documentation.md#configuration
  const int community_hop_radius;
  const std::string node_label;
  const int hop_forward_limit_ping;
  const int hop_forward_limit_chunk;
  const int max_inbound_queue_time;
  // ^^ END ^^
public:
  // The public key credential
	std::string getPublicKey() const;
  
  // Count of known path segments
  unsigned int getKnownPathCount() const;

  // Count of all known remote nodes
  unsigned int getRemoteNodesCount() const;
};

#endif
