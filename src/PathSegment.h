/*
@description A path segment is a connection between exactly two nodes
@author Jakson R. Kallio
*/

#ifndef PATHSEGMENT_
#define PATHSEGMENT_

#include <vector>

class PathSegment {
private:
  // The two nodes acting as endpoints for the segment.	
  RemoteNode* endpoint_a;
  RemoteNode* endpoint_b;
  
  // Last time, in epoch seconds, checked
  unsigned int last_check;

  // Failed attempts to send chunks across this PathSegment
  unsigned int failed_attempts;

  // Failed attempts (since last success) to send chunks across this PathSegment
  unsigned int recent_failed_attempts;

  // Successful attempts to send chunks across this PathSegment
  unsigned int success_attempts;
public:
  PathSegment(RemoteNode* endpoints[2]);  

  // Get the two endpoint nodes
  RemoteNode* getEndpointNodes() const;
  
  // Total times this PathSegment was used
  unsigned int getTotalUsage() const;

  // Rate of recent failures per the total
  double getRecentFailureRate() const;

  // Rate of total success per the total
  double getSuccessRate() const;
};

#endif
