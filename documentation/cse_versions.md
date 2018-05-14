# CSE Versions
A **controlled simulation environment (CSE)** is a contrived network used for testing mecono functionality. The usefulness comes from using an identical network across multiple simulation rounds for bug finding, feature testing, and statistics gathering.

## CSE v1
Eight nodes, four parcels. Two parcels have an ideal path of length two (the lowest possible) and the others have longer paths. There is one inactive adversarial node.

## CSE v2
100 nodes, 50 parcels. Four integer arrays are used to create neighborships. Example, if there were 10 nodes, the neighborships arrays may look like this:
```
[3, 2, 1, 3, 3, 2, 1, 1, 2]
[1, 3, 4, 2, 0, 4, 0, 4, 0]
[2, 1, 2, 1, 3, 2, 3, 1, 0]
[1, 0, 0, 0, 4, 2, 2, 2, 1]
```
The number specifies the offset from the current node to the neighbor node.  Which means, for example, that node `n0` is connected to `n3` (`n0 + 3`), `n1` (`n0 + 1`), and `n2` (`n0 + 2`). There may be no duplicates, and if a neighborship value is zero, it doesn't exist. A modulo is used if one of the last nodes goes after the end of the node array. The number within the neighborship arrays are between 0 and 4 (the number of neighborship arrays). The neighborship arrays are randomly generated only once at the time of code writing and are thereforce hardcoded to be the same for all future simulations.

The 50 parcels are stored in an array like so:
```
[32, 20],
[27, 19],
[18, 49],
...
```
Whereas the first value is the node, denoted by the index in the `node_set` array. Each parcel's data content is the string `"parcel_data_" + n ` where `n` is the parcel array index.
