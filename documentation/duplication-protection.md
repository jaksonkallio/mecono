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

## Improving the Solution
Due to the way the valid sequence number consolidation happens, large data payloads will need to be retried if they are in the process of being sent when a consolidation happens.

```
received_sequence_numbers array = [5,6,7,8,10]
min_sequence_number = 4
```

Consolidation happens because the array is size 5.

```
received_sequence_numbers array = []
min_sequence_number = 10
```

Now, when the missing parcel 9 is received, it will be invalid. Sender will wait for it to go stale, then will re-send with a higher sequence number. There are a couple improvements to minimize this issue.

Upon receiving a parcel, if `parcel_sequence_number == (min_sequence_number + 1)` then we will simply increment `min_sequence_number`. This will shrink the size of the `received_sequence_numbers` array.

Also, occasionally scan `received_sequence_numbers` for values that are `received_sequence_numbers.get(i) == (min_sequence_number + 1)` and remove index `i` while updating the `min_sequence_number`. This will greatly reduce `received_sequence_numbers` array because it will only fill with sequence numbers with gaps between them, but these should fill in quickly because theoretically, if one data parcel gets somewhere, another shouldn't have a problem a few milliseconds after. We will still keep "hard" consolidation in place, but this shouldn't happen nearly as frequently as consolidation without the improvements.

## Running out of sequence numbers?
Java long type has a max value of 9,223,372,036,854,775,807.  If you communicated with a specific remote node every nanosecond (billion times per second), it would take 292 years before you'd exhaust sequence numbers. Once this limit is reached, simply change your node address.
