package mecono.node;

/**
 *
 * @author jak
 */
public interface Node {

	public String getAddress();

	public String getLabel();
	
	public static boolean isValidAddress(String address){
		return true;
	}
}
