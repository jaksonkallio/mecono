package mecono;

/**
 *
 * @author Jakson Kallio
 */
public class Mecono {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean sandbox = false;
		boolean simulated_network = true;
		
		if(sandbox){
			sandbox();
		}else{
			/*for (String s: args) {
				if (s.equals("--sim")) {
					SimNetwork sim = new SimNetwork();
					sim.begin();
				}
			}*/
			
			if(simulated_network){
				SimNetwork sim = new SimNetwork();
				sim.begin();
			}
        }
    }
	
	public static void sandbox(){
		
	}
}
