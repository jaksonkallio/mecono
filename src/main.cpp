/*
@description Program driver file
@author Jakson R. Kallio
*/

#include <iostream>

#include "SimNetwork.h"


int main(){
	SimNetwork* sim = new SimNetwork();

	sim->drawNetworkGrid();

  return EXIT_SUCCESS;
}
