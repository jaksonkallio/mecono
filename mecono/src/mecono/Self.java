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
import java.util.concurrent.LinkedBlockingQueue;
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
        parcel.process();
    }
    
    public Trigger lookupTrigger(String id) throws InsufficientKnowledgeException {
        if(triggers.containsKey(id)){
            return triggers.get(id);
        }
        
        throw new InsufficientKnowledgeException("Unrecognized trigger parcel");
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
            if(parcel.ready()){
                try {
                    getHardwareController().send(parcel.serialize(), self_node);
                    parcel.logSend();
                }catch(InsufficientKnowledgeException ex){
                    log(ErrorLevel.ERROR, "Cannot send parcel despite being ready", ex.getMessage());
                }
            }
        }
    }
    
    public void processForwardQueue(){
        
    }
    
    public void log(ErrorLevel error_level, String message, String detail){
        log(error_level, message + ": " + detail);
    }
    
    public void log(ErrorLevel error_level, String message){
        System.out.println("[" + error_level.name() + "] " + message);
    }
    
    private void cleanup(){
        last_cleanup = Util.time();
        pruneTriggerHistory();
    }
    
    public void enqueueSend(Foreign parcel){
        forward_queue.offer(parcel);
    }
    
    public void enqueueSend(Terminus parcel){
        send_queue.add(parcel);
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
    
	private final Node self_node;
	private final HashMap<String, Node> node_memory;
    private final HashMap<String, Trigger> triggers;
    private final List<Terminus> send_queue;
    private final Queue<Foreign> forward_queue;
	private HardwareController hc;
	private final KeyPair key_pair;
    private long last_cleanup;
}
