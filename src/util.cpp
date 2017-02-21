#ifndef MECONO_UTIL_
#define MECONO_UTIL_

#include <stdlib.h>
#include <time.h>

bool rnum_seeded = false;

unsigned short int randPercent(){
	if(!rnum_seeded){
		srand(time(NULL));
		rnum_seeded = true;
	}

	return (rand() % 101);
}

#endif
