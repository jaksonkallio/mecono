package mecono;

/** 
 * @description An action potential waiting for a response from a node after initially sending out a signal.
 * @author jak
 */
public class UponResponseAction {
	
	public UponResponseAction(Mailbox mailbox, Pallet original_pallet) {
		this.mailbox = mailbox;
		this.original_pallet = original_pallet;
		this.response_type = determineResponseType();
		this.original_time_sent = Protocol.getEpochSecond();
	}
	
	@Override
	public boolean equals(Object o){
		UponResponseAction other = (UponResponseAction) o;
		
		return (other.getPalletID().equals(this.getPalletID()) && other.getResponseType() == this.getResponseType());
	}
	
	public void giveResponsePallet(Pallet response_pallet){
		if(response_pallet.getPalletType() == response_type && response_pallet.getPalletID().equals(original_pallet.getPalletID())){
			// The response pallet is indeed the response to the original sent pallet
			this.response_pallet = response_pallet;
			responded = true;
		}
	}
	
	public void runAction(){
		if(responded){
			switch(getResponseType()){
				case PING_RESPONSE:
					actionFromPing();
					break;
				case FIND_RESPONSE:
					actionFromFind();
					break;
				case DATA_RECEIPT:
					actionFromData();
					break;
				default:
					break;
			}
		}
	}
	
	public String getPalletID(){
		return original_pallet.getPalletID();
	}
	
	public PalletType getResponseType(){
		if(responded){
			return response_pallet.getPalletType();
		}else{
			return response_type;
		}
	}
	
	/**
	 * What do do when a ping is responded to.
	 */
	private void actionFromPing(){
		// TODO: Verify that the destination signed the original pallet
		original_pallet.getDestination().updateSuccessfulPing((int) (Protocol.getEpochSecond() - original_pallet.getTimeSent()));
	}
	
	private void actionFromFind(){
		// TODO: What to do after a find is responded to
	}
	
	private void actionFromData(){
		// TODO: What to do after data was successfully received remotely
	}
	
	private PalletType determineResponseType(){
		switch(original_pallet.getPalletType()){
			case PING:
				return PalletType.PING_RESPONSE;
			case FIND:
				return PalletType.FIND_RESPONSE;
			case DATA:
				return PalletType.DATA_RECEIPT;
			default:
				return PalletType.UNKNOWN;
		}
	}
	
	private Mailbox mailbox;
	private Pallet original_pallet;
	private Pallet response_pallet;
	private boolean responded = false;
	private final long original_time_sent;
	private final PalletType response_type;
}
