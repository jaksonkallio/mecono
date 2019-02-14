package mecono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import node.Address;
import node.Node;

public class Self {
	public Self(KeyPair key_pair){
		this.key_pair = key_pair;
		this.self_node = new Node(new Address(this.key_pair.getPublic().toString()));
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
	
	public Node getSelfNode(){
		return self_node;
	}
	
	public static final int KEY_LENGTH = 1024;
	
	private final Node self_node;
	private final KeyPair key_pair;
}
