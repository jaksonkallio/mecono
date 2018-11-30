package mecono.node;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

// SECURITY WARNING: I'm not a cryptography expert! This code is a placeholder for a potentially more secure system! It is implemented as-is for purposes of testing.

public class CryptoManager {
	public CryptoManager(){
		genKeys();
	}
	
	public CryptoManager(PrivateKey priv_key, PublicKey pub_key){
		this.priv_key = priv_key;
		this.pub_key = pub_key;
	}
	
	public String getPublicKey(){
		return pub_key.toString();
	}
	
	public boolean verifySig(String signature, Node node){
		// TODO: Actually do a verification of the signature with the node's address
		return true;
	}
	
	public String decrypt(String message){
		// TODO: Actual decryption
		return message;
	}
	
	public String sign(String message) {
		try{
			Signature dsa = Signature.getInstance("SHA1withECDSA");
			dsa.initSign(getPrivateKey());
			byte[] message_bytes = message.getBytes("UTF-8");
			dsa.update(message_bytes);
			byte[] signature = dsa.sign();
			return new BigInteger(1, signature).toString(16);
		} catch(InvalidKeyException | NoSuchAlgorithmException | SignatureException | UnsupportedEncodingException ex){
			
		}
		
		return "";
	}
	
	private void genKeys(){
		try{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

			keyGen.initialize(256, random);

			KeyPair pair = keyGen.generateKeyPair();
			priv_key = pair.getPrivate();
			pub_key = pair.getPublic();
		} catch (NoSuchAlgorithmException ex) {
			
		}
		
	}
	
	private PrivateKey getPrivateKey(){
		return priv_key;
	}
	
	private PrivateKey priv_key;
	private PublicKey pub_key;
}
