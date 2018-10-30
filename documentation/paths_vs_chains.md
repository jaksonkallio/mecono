# Paths vs. Chains
A **chain** is a simple, isolated, final, data type representing a connections between nodes. For example, a chain might look like "A->B->C->D". On the other hand, a **path** is a chain bundled with usage statistics. For the most part, paths are not shared because there is no way to trust another node's usage statistics of a path. Chains are meant to be freely shared, and chains are the data type attached to serialized parcels.

It is conventional for Mecono objects to use `Path` instead of `Chain` for all variables except in a few cases, such as serialization of parcels or modifying `Path` object code.
