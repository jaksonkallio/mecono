# Duplication Protection

## Duplication Issue
Duplication is bound to happen in Mecono communications. This issue is solved in traditional internet protocols by using a sequence number to identify packets in a stream. Because of the zero-trust, stateless, mesh-based environment of Mecono, duplicate parcels will happen much more frequently from both normal operation and attackers. Duplicate parcels without protections could have the effect of running remote actions several times, such as sending an instant message or completing a bank transaction.

## Bad Solution: Attached Unix Time
Since only the sender is able to construct a cryptographically-valid parcel, they could attach a Unix time that is then checked by the receiver. This won't work well for a few reasons:
- Time may not be exactly the same between sender/receiver.
- The amount of time it takes for a parcel to get to destination shouldn't matter.
- There is a valid window of time where duplicates could be received successfully.

## Solution to be Implemented
A better solution would be to store a long integer `sequence_number` along with every `RemoteNode`. Whenever we send a Parcel to the destination (including retries), we increment the attached `sequence_number`. The receiver will then store a copy of all sequence numbers that they process from a specific remote node, and will ignore duplicates. After a certain amount of time, to save space, receiver will consolidate their sequence numbers into a `min_sequence_number` which is equal to the largest stored sequence number. In an essence, we ignore any parcel that meets any of the following conditions:
- `parcel_sequence_number <= min_sequence_number`
- `received_sequence_numbers.contains(parcel_sequence_number)`
