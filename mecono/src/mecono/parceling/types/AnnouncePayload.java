/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mecono.parceling.types;

import mecono.node.Neighbor;
import mecono.node.Path;
import mecono.node.SelfNode;
import mecono.parceling.BadPathException;
import mecono.parceling.Parcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.parceling.Payload;
import mecono.parceling.PayloadType;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author sabreok
 */
public class AnnouncePayload extends Payload {

	public AnnouncePayload() throws BadProtocolException, MissingParcelDetailsException {
		setAnnounceChainFromHistory();
	}

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();
		
		SelfNode self = getParcel().getMailbox().getOwner();

		try {
			self.learnPath(getAnnounceChain(), null);

			for (Neighbor neighbor : self.getNeighbors()) {
				// Create a new forwarded announce parcel
			}
		} catch (BadPathException ex) {
			//TODO: getMailbox().getOwner().nodeLog();
		}

	}
	
	public PayloadType getPayloadType(){
		return PayloadType.ANNC;
	}

	private void setAnnounceChainFromHistory() throws BadProtocolException, MissingParcelDetailsException {
		// Verify that the last stop is us
		if (!announce_chain.getLastStop().equals(getParcel().getMailbox().getOwner())) {
			throw new BadProtocolException("Received announce parcel with an invalid last stop");
		}

		// TODO: Check signatures to verify that the path is signed by each node
		announce_chain = new Path(getParcel().getPath());
	}

	private Path getAnnounceChain() {
		return announce_chain;
	}

	private int getAnnounceChainLength() {
		return announce_chain.getPathLength();
	}

	private Path announce_chain;
}
