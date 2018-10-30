# Success Statistics
One important function of a Mecono node is to keep statistics about the success in contacting specific nodes, how helpful nodes are, and which specific paths are functional. These statistics are stored in different classes.

* **Assist**: An assist is a simple incremented integer stored in each `RemoteNode`. Every time a parcel is received, we give an assist to each node in the node chain attached to the parcel.
* **Successes/Failures**: Each path has statistics about the contained node chain, relative to self. Successes/failures come into play when we are sending parcels as the originator. When sending, a handshake is created as we await a response. If the handshake goes stale, (a.k.a. "timeout") we increment failures on the path. If we receive a response, we increment successes for the path.
* **Reliability**: Reliability is a specially calculated score to judge, on a scale of 0.0-1.0, how reliable the path is. If a path has fewer than 5 attempted uses, the path gets constant reliability score of 0.75 regardless of actual success rate. If a path has more than 5 attempted uses, the formula for determining reliability is as follows:

`reliability = (successes() * (1 + PATH_RELIABILITY_BONUS)) / totalUses();`

Where `PATH_RELIABILITY_BONUS = 0.05` by default. Effectively, this formula will "forgive" about 5% of failures. This means that long-standing successful paths won't be unfairly hit if they go down temporarily. When there are multiple long-standing successful paths, small differences in "actual" reliability score shouldn't matter: they should remain equally reliable at the max score of 1.0 .
