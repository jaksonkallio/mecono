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
        for (String s: args) {
            if (s.equals("--sim")) {
                SimNetwork sim = new SimNetwork();
                break;
            }
        }
    }
}