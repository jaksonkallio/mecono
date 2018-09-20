/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mecono.parceling.types;

import mecono.node.Mailbox;
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
	
	public AnnounceParcel(Mailbox mailbox, TransferDirection direction) {
		super(mailbox, direction);
	}
	
	@Override
	public void onReceiveAction() throws BadProtocolException, MissingParcelDetailsException {
		super.onReceiveAction();
		
		try {
			getMailbox().getOwner().learnPath(getAnnounceChain(), null);
		}catch(BadPathException ex){
			//TODO: getMailbox().getOwner().nodeLog();
		}
		
		
	}
	
	public void setAnnounceChain(Path announce_chain){
		this.announce_chain = announce_chain;
	}
	
	private Path getAnnounceChain(){
		return announce_chain;
	}
	
	private Path announce_chain;
}
