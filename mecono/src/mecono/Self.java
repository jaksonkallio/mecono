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
import node.BadProtocolException;
import node.Chain;
import node.InsufficientKnowledgeException;
import node.Node;
import parcel.Foreign;
import parcel.Parcel;
import parcel.Terminus;
import parcel.Trigger;

public class Self {
	public Self(KeyPair key_pair){
		this.key_pair = key_pair;
		this.self_node = new Node(this);
		this.node_memory = new HashMap<>();
        this.triggers = new HashMap<>();
		this.self_node.setPublicKey(key_pair.getPublic().toString());
        this.send_queue = new ArrayList<>();
        this.forward_queue = new LinkedBlockingQueue<>();
		this.friends = new ArrayList<>();
        this.node_log = new LinkedBlockingQueue<>();
		this.rng = new Random();
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
		return getInternalAddress() + " @ " + getSelfNode().getCoords().toString();
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
	
	public Node getSelfNode(){
		return self_node;
	}
	
	public Node lookupNode(String address){
        if(node_memory.containsKey(address)){
            return node_memory.get(address);
        }
        
        Node new_node = new Node(this);
        new_node.setAddress(address);
        node_memory.put(new_node.getAddress(), new_node);
        
        return new_node;
	}
	
	public static long time(){
		return System.currentTimeMillis();
	}
    
    public void receive(Parcel parcel){
        parcel.receive();
    }
    
    public Trigger lookupTrigger(String id) throws InsufficientKnowledgeException {
        if(triggers.containsKey(id)){
            return triggers.get(id);
        }
        
        throw new InsufficientKnowledgeException("Unrecognized trigger parcel");
    }
	
	public void learn(Chain chain){
		
	}
    
    public void work(){
        if(Util.timeElapsed(last_cleanup) > CLEANUP_INTERVAL){
            cleanup();
        }
        
        processSendQueue();
        processForwardQueue();
    }
    
    public void processSendQueue(){
        for(Terminus parcel : send_queue){
			try {
				// Check if we have a destination
				if(parcel.getDestination() == null){
					continue;
				}

				// Check if the chain is non-null
				if(parcel.getChain() == null){
					// If null, try to find a chain
					parcel.setChain(getSelfNode().find(parcel.getDestination()));
					
					// If still null, consult some friends for network information
					if(parcel.getChain() == null){
						parcel.getDestination().findMe();
					}
				}
				
				// Make sure the chain is online
				if(!parcel.getChain().online()){
					// Not online, test the chain
					parcel.getChain().test();
				}
				
				getHardwareController().send(parcel.serialize(), self_node);
				parcel.logSend();
			}catch(InsufficientKnowledgeException | BadProtocolException ex){
				log(ErrorLevel.ERROR, "Cannot send parcel", ex.getMessage());
			}
        }
    }
    
    public void processForwardQueue(){
        
    }
    
    public HashMap<String, Node> getNodeMemory(){
        return node_memory;
    }
    
    public void log(ErrorLevel error_level, String message, String detail){
        log(0, error_level, message + ": " + detail);
    }
    
    public void log(ErrorLevel error_level, String message){
        log(0, error_level, message);
    }
    
    public void log(int indent, ErrorLevel error_level, String message){
        String construct = "";
        
        for(int i = 0; i < indent; i++){
            construct += "  ";
        }
        
        construct += "[" + error_level.name() + "] " + message;
        
        System.out.println(construct);
        node_log.offer(construct);
    }
    
    private void cleanup(){
        last_cleanup = Self.time();
        pruneTriggerHistory();
    }
    
    public void enqueueSend(Foreign parcel){
        forward_queue.offer(parcel);
    }
    
    public void enqueueSend(Terminus send_parcel){
		// Duplicates only matter if we are sending trigger parcels
		if(send_parcel instanceof Trigger){
			for(Terminus parcel : send_queue){
				if(parcel.isDuplicate(send_parcel)){
					return;
				}
			}
		}
		
        send_queue.add(send_parcel);
    }
	
	public void addFriend(Node node){
		if(!friends.contains(node)){
			friends.add(node);
		}
	}
	
	public List<Node> getFriends(){
		List<Node> results = new ArrayList<>(friends);
		
		for(Node neighbor : getSelfNode().getNeighbors()){
			results.add(neighbor);
		}
		
		return friends;
	}
    
    public void printOutbox(){
        log(ErrorLevel.INFO, "Outbox");
        log(1, ErrorLevel.INFO, "Count: " + send_queue.size());
        log(1, ErrorLevel.INFO, "List:");
        for(int i = 0; i < send_queue.size() && i < 20; i++){
            Terminus parcel = send_queue.get(i);
            log(2, ErrorLevel.INFO, parcel.getID() + " " + parcel.getParcelType().name() + " " + Util.fuzzyTime(Util.timeElapsed(parcel.getTimeQueued())));
        }
    }
    
    private void pruneTriggerHistory(){
        for(Map.Entry<String, Trigger> entry : triggers.entrySet()) {
            String key = entry.getKey();
            Trigger trigger = entry.getValue();
            
            // If the trigger has response or we're tired of waiting for a response
            if(trigger.isResponded() || (trigger.isSent() && Util.timeElapsed(trigger.getTimeSent()) > MAX_RESPONSE_WAIT)){
                triggers.remove(entry.getKey());
            }
        }
    }
	
	public static final int KEY_LENGTH = 1024;
    public static final long MAX_RESPONSE_WAIT = 120000; // 2 minutes
    public static final long CLEANUP_INTERVAL = 30000; // 30 seconds
	public static final short INTERNAL_ADDRESS_LEN = 4;
	public static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	public final Random rng;
	private final Node self_node;
	private final HashMap<String, Node> node_memory;
	private final List<Node> friends;
    private final HashMap<String, Trigger> triggers;
    private final Queue<String> node_log;
    private final List<Terminus> send_queue;
    private final Queue<Foreign> forward_queue;
	private HardwareController hc;
	private final KeyPair key_pair;
    private long last_cleanup;
	private String internal_address; // Internal addresses are used for internal identification, much like an internal IP address
}
