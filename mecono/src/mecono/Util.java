package mecono;

public class Util {
	public static long time(){
		return System.currentTimeMillis();
	}
    
    public static long timeElapsed(long start){
        return time() - start;
    }
}
