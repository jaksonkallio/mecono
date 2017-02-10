/*
@description Program driver file
@author Jakson R. Kallio
*/

#include <iostream>

#include "RemoteNode.h"
#include "SimNode.h"
#include "PathSegment.h"
#include "Path.h"
#include "SelfNode.h"
#include "SimNetwork.h"


int main(){
	SimNetwork* sim = new SimNetwork();

	sim->drawNetworkGrid();

  return EXIT_SUCCESS;
}
