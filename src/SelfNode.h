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
	unsigned short int community_hop_radius = 3;
	std::string node_label = "My Node";
	unsigned int hop_forward_limit_ping = 30;
	unsigned int hop_forward_limit_chunk = 10;
	unsigned int max_inbound_queue_time = 30000;
	// ^^ END ^^

	// Known remote nodes
	struct RemoteNodeInfo {
		RemoteNode* theNode;
		bool is_neighbor;
		unsigned int last_latency;
		Path* pathTo;
	};
	std::vector<RemoteNodeInfo> known_nodes;
public:
  // The public key credential
	std::string getPublicKey() const;

  // Count of known path segments
  unsigned int getKnownPathCount() const;

  // Count of all known remote nodes
  unsigned int getRemoteNodesCount() const;
};

#endif
