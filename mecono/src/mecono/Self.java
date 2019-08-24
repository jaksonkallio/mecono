package mecono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import node.AdjacencyList;
import node.BadProtocolException;
import node.Chain;
import node.Connection;
import node.InsufficientKnowledgeException;
import node.MNode;
import parcel.Foreign;
import parcel.Parcel;
import parcel.Terminus;
import parcel.Trigger;

public class Self {
	public Self(KeyPair key_pair){
		this.key_pair = key_pair;
		this.self_node = new MNode(this);
        this.triggers = new HashMap<>();
		this.self_node.setPublicKey(key_pair.getPublic());
        this.send_queue = new ArrayList<>();
        this.forward_queue = new LinkedBlockingQueue<>();
		this.friends = new ArrayList<>();
        this.node_log = new LinkedBlockingQueue<>();
		this.rng = new Random();
		this.node_database = new NodeDatabase(this);
		genInternalAddress();
	}
    
	public String genRandomString(int k){
		char[] text = new char[k];

		for (int i = 0; i < k; i++) {
			text[i] = HEX_CHARS[rng.nextInt(HEX_CHARS.length)];
		}

		return new String(text);
	}
	
	public final void genInternalAddress(){
		setInternalAddress(genRandomString(INTERNAL_ADDRESS_LEN));
	}
	
	public void setInternalAddress(String internal_address){
		this.internal_address = internal_address;
	}
	
	public String getInternalAddress(){
		return internal_address;
	}
	
	@Override
	public String toString(){
		//return getInternalAddress() + " @ " + getSelfNode().getCoords().toString();
        return getSelfNode().getTrimmedAddress();
    }
	
    @Override
    public boolean equals(Object o){
        if(o instanceof Self){
            Self other = (Self) o;
            
            if(getSelfNode().equals(other.getSelfNode())){
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public int hashCode(){
        return getSelfNode().hashCode();
    }
	
	public static Self generate() throws NoSuchAlgorithmException{
		KeyPairGenerator key_gen;
		KeyPair pair;
		PrivateKey priv_key;
		PublicKey pub_key;
		
		key_gen = KeyPairGenerator.getInstance("RSA");
		key_gen.initialize(KEY_LENGTH);
		pair = key_gen.generateKeyPair();
		priv_key = pair.getPrivate();
		pub_key = pair.getPublic();
		
		return new Self(pair);
	}
	
	public void setHardwareController(HardwareController hc){
		this.hc = hc;
	}
	
	public HardwareController getHardwareController(){
		return hc;
	}
	
	public MNode getSelfNode(){
		return self_node;
	}
	
	public static long time(){
		return System.currentTimeMillis();
	}
    
    public void receive(Parcel parcel) {
		try {
			parcel.receive();
		} catch(BadProtocolException ex){
			log(ErrorLevel.ERROR, "Could not receive parcel", ex.getMessage());
		}
    }
    
    public Trigger lookupTrigger(String id) throws InsufficientKnowledgeException {
        if(triggers.containsKey(id)){
            return triggers.get(id);
        }
        
        throw new InsufficientKnowledgeException("Unrecognized trigger parcel");
    }
	
	public void learn(Chain chain){
		MNode prev = null;
		
		for(MNode node : chain.getNodes()){
			if(prev == null){
				continue;
			}
			
			prev.addConnection(node);
			
			prev = node;
		}
	}
	
	public void learn(AdjacencyList adj_list){
		for(AdjacencyList.AdjacencyItem adjacency_item : adj_list.adjacency_items){
			for(MNode target : adjacency_item.targets){
				adjacency_item.source.addConnection(target);
			}
		}
	}
	
    public void work(){
        if(Util.timeElapsed(last_cleanup) > CLEANUP_INTERVAL){
            cleanup();
        }
		
		if(Util.timeElapsed(last_ping_local_group) > PING_LOCAL_GROUP_INTERVAL){
            pingLocalGroup();
        }
        
        processSendQueue();
		
		try {
			processForwardQueue();
		}catch(BadProtocolException ex){
			log(ErrorLevel.ERROR, "Could not process forwarding queue", ex.getMessage());
		}
	}
	
	public NodeDatabase getNodeDatabase(){
		return node_database;
	}
    
    public void processSendQueue(){
        for(int i = 0; i < send_queue.size(); i++){
            Terminus parcel = send_queue.get(i);
            
			try {
				// Check if we have a destination
				if(parcel.getDestination() == null){
					continue;
				}

				// Check if the chain is non-null
				if(parcel.getChain() == null){
					// If null, try to find a chain
					parcel.setChain(getSelfNode().find(parcel.getDestination()));
				}
				
				// If still null, consult some friends for network information
				if(parcel.getChain() == null){
					parcel.getDestination().findMe();
					continue;
				}
				
				// Make sure the chain is online
				if(parcel.requireOnlineChain() && !parcel.getChain().online()){
					// Not online, test the chain
					parcel.getChain().test();
					continue;
				}
				
				// Remove it from the send queue
				send_queue.remove(i);
				forward_queue.offer(parcel);
				
				log(ErrorLevel.OK, "Parcel sent", parcel.toString());
			}catch(BadProtocolException ex){
				log(ErrorLevel.ERROR, "Cannot send parcel", ex.getMessage());
				break;
			}
        }
    }
    
    public void processForwardQueue() throws BadProtocolException {
        while(forward_queue.size() > 0){
			Parcel parcel = forward_queue.poll();
			
			MNode next_node = parcel.getChain().getNext();
			if(next_node == null){
				throw new BadProtocolException("Could not determine a next node in parcel chain");
			}

			getHardwareController().send(parcel.serialize(), next_node);
			
			if(parcel instanceof Trigger){
				((Trigger) parcel).logSend();
				addTriggerHistory((Trigger) parcel);
			}
		}
    }
    
	public void pingLocalGroup(){
		last_ping_local_group = Self.time();
		
		for(MNode local_node : getSelfNode().getGroup().getNodeList()){
			local_node.findMe();
		}
	}
	
    public void log(ErrorLevel error_level, String message, String detail){
        log(0, error_level, message + ": " + detail);
    }
    
    public void log(ErrorLevel error_level, String message){
        log(0, error_level, message);
    }
    
    public void log(int indent, ErrorLevel error_level, String message){
        String construct = "";
        
        construct += "[" + getSelfNode().getTrimmedAddress() + "]";
        construct += "[" + error_level.name() + "] ";
		
		for(int i = 0; i < indent; i++){
            construct += "  ";
        }
		
		construct += message;
        
        System.out.println(construct);
        
        node_log.offer(construct);
		
		while(node_log.size() > 1000){
			node_log.remove();
		}
    }
    
    private void cleanup(){
        last_cleanup = Self.time();
        pruneTriggerHistory();
        pruneSendHistory();
    }
    
    public void enqueueSend(Foreign parcel){
        forward_queue.offer(parcel);
    }
    
	public void addTriggerHistory(Trigger trigger){
		triggers.put(trigger.getID(), trigger);
	}
	
    public void enqueueSend(Terminus send_parcel){
		// Duplicates only matter if we are sending trigger parcels
		if(send_parcel instanceof Trigger){
			// Check the send queue
			for(Terminus parcel : send_queue){
				if(parcel.isDuplicate(send_parcel)){
					return;
				}
			}
			
			// Check the trigger history
			for(Map.Entry<String, Trigger> entry : triggers.entrySet()) {
				String key = entry.getKey();
				Trigger trigger = entry.getValue();

				if(trigger.isDuplicate(send_parcel)){
					return;
				}
			}
		}
		
        log(ErrorLevel.OK, "Enqueued", send_parcel.toString());
        
        send_queue.add(send_parcel);
    }
	
	public void addFriend(MNode node){
		if(!friends.contains(node)){
			friends.add(node);
		}
	}
	
	public List<MNode> getFriends(){
		List<MNode> results = new ArrayList<>(friends);
		
		for(MNode neighbor : getSelfNode().getNeighbors()){
			results.add(neighbor);
		}
		
		return results;
	}
    
    public void printOutbox(){
        log(ErrorLevel.INFO, "Outbox");
        log(1, ErrorLevel.INFO, "Count: " + send_queue.size());
        log(1, ErrorLevel.INFO, "List:");
        for(int i = 0; i < send_queue.size() && i < 100; i++){
            Terminus parcel = send_queue.get(i);
            log(2, ErrorLevel.INFO, parcel.getID() + " " + parcel.getParcelType().name() + " " + Util.fuzzyTime(Util.timeElapsed(parcel.getTimeQueued())));
        }
    }
    
	public void printNodes(){
		log(ErrorLevel.INFO, "Nodes");
        log(1, ErrorLevel.INFO, "Count: " + node_database.getNodeKnowledgeCount());
        log(1, ErrorLevel.INFO, "List:");
		
		MNode[] top_nodes = node_database.getTopNodes(10);
		
        for(int i = 0; i < top_nodes.length; i++){
            MNode node = top_nodes[i];
			
			if(node == null){
				continue;
			}
			
            log(2, ErrorLevel.INFO, node.toString());
        }
	}
	
    private void pruneTriggerHistory(){
		ArrayList<String> keys_to_remove = new ArrayList<>();
		
        for(Map.Entry<String, Trigger> entry : triggers.entrySet()) {
            String key = entry.getKey();
            Trigger trigger = entry.getValue();
            
            // If the trigger has response or we're tired of waiting for a response
            if(trigger.isResponded() || (trigger.isSent() && Util.timeElapsed(trigger.getTimeSent()) > MAX_RESPONSE_WAIT)){
                
				keys_to_remove.add(key);
            }
        }
		
		for(String key : keys_to_remove){
			triggers.remove(key);
		}
    }
    
    private void pruneSendHistory(){
        for(int i = (send_queue.size() - 1); i >= 0; i--){
            if(send_queue.get(i).isSent() && Util.timeElapsed(send_queue.get(i).getTimeSent()) > MAX_RESPONSE_WAIT){
                send_queue.remove(i);
            }
        }
    }
	
	public static final int KEY_LENGTH = 1024;
    public static final long MAX_RESPONSE_WAIT = 120000; // 2 minutes
    public static final long CLEANUP_INTERVAL = 30000; // 30 seconds
	public static final long PING_LOCAL_GROUP_INTERVAL = (long)(Connection.ONLINE_THRESHOLD * 0.90); // Use the online threshold, minus a little bit to preemptively ping connections
	public static final short INTERNAL_ADDRESS_LEN = 4;
	public static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	public static final int LOCAL_GROUP_RADIUS = 4;
	
	public final Random rng;
	private final NodeDatabase node_database;
	private final MNode self_node;
	private final List<MNode> friends;
    private final HashMap<String, Trigger> triggers;
    private final Queue<String> node_log;
    private final List<Terminus> send_queue;
    private final Queue<Parcel> forward_queue;
	private HardwareController hc;
	private final KeyPair key_pair;
    private long last_cleanup;
	private long last_ping_local_group;
	private String internal_address; // Internal addresses are used for internal identification, much like an internal IP address
}
