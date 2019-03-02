package mecono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import node.InsufficientKnowledgeException;
import node.Node;
import parcel.Parcel;
import parcel.Trigger;

public class Self {
	public Self(KeyPair key_pair){
		this.key_pair = key_pair;
		this.self_node = new Node(this);
		this.node_memory = new HashMap<>();
        this.triggers = new HashMap<>();
		this.self_node.setPublicKey(key_pair.getPublic().toString());
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
		
		System.out.println(priv_key.getEncoded());
		System.out.println(pub_key.getEncoded());
		
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
		return node_memory.get(address);
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
    
    public void log(ErrorLevel error_level, String message, String detail){
        log(error_level, message + ": " + detail);
    }
    
    public void log(ErrorLevel error_level, String message){
        System.out.println("[" + error_level.name() + "] " + message);
    }
	
	public static final int KEY_LENGTH = 1024;
	
	private final Node self_node;
	private final HashMap<String, Node> node_memory;
    private final HashMap<String, Trigger> triggers;
	private HardwareController hc;
	private final KeyPair key_pair;
}
