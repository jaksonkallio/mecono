project(Mecono)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -std=c++14")

add_executable(
	mecono
	main.cpp
	RemoteNode.cpp
	SimNode.cpp
	PathSegment.cpp
	Path.cpp
	SelfNode.cpp
	SimNetwork.cpp
)
