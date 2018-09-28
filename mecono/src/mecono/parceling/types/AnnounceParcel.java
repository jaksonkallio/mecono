/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mecono.parceling.types;

import mecono.node.Mailbox;
import mecono.node.Neighbor;
import mecono.node.Path;
import mecono.parceling.BadPathException;
import mecono.parceling.DestinationParcel;
import mecono.parceling.MissingParcelDetailsException;
import mecono.protocol.BadProtocolException;

/**
 *
 * @author sabreok
 */
public class AnnounceParcel extends DestinationParcel {

	public AnnounceParcel(Mailbox mailbox, TransferDirection direction) throws BadProtocolException, MissingParcelDetailsException {
		super(mailbox, direction);
		setAnnounceChainFromHistory();
	}

	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();

		try {
			getMailbox().getOwner().learnPath(getAnnounceChain(), null);

			for (Neighbor neighbor : getMailbox().getOwner().getNeighbors()) {
				// Create a new forwarded announce parcel
			}
		} catch (BadPathException ex) {
			//TODO: getMailbox().getOwner().nodeLog();
		}

	}

	private void setAnnounceChainFromHistory() throws BadProtocolException, MissingParcelDetailsException {
		// Verify that the last stop is us
		if (!announce_chain.getLastStop().equals(getMailbox().getOwner())) {
			throw new BadProtocolException("Received announce parcel with an invalid last stop");
		}

		// TODO: Check signatures to verify that the path is signed by each node
		announce_chain = new Path(getPathHistory());
	}

	private Path getAnnounceChain() {
		return announce_chain;
	}

	private int getAnnounceChainLength() {
		return announce_chain.getPathLength();
	}

	private Path announce_chain;
}
