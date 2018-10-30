# Steps to Send Parcel
1. Create a parcel object and supply it a destination.
2. Create a payload of the desired type and give it to the parcel.
3. Hand the parcel off to the handshake manager. The mailbox will have a reference to the handshake manager.
4. The handshake manager (`HandshakeHistory`) will repeatedly check if the parcel is ready to be sent, and will send the necessary find or ping parcels to make the parcel ready to be sent.
5. Once the handshake manager notices that the parcel is ready for sending, the parcel is removed from the pending list and is given to the mailbox outbound queue.
6. The mailbox will eventually process the parcel in the outbound queue. The parcel is then handed off to the network controller, which will read the next node, serialize, and then send it across the appropriate neighbor port.
7. The handshake manager will continuously recheck to see if there is a response yet to the handshake. After a "stale" time, the parcel is resent. After several resend attempts, the parcel is considered "failed", updating the path statistics accordingly.
