package mecono;

/**
 *
 * @author jak
 */
public class RemoteNode implements Node {
    	
	public String getAddress() {
		return address;
	}
	
	public String getLabel() {
		return label;
	}
	
    private String address;
	private String label;
}