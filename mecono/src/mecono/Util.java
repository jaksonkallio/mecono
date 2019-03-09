package mecono;

public class Util {
	public static long time(){
		return System.currentTimeMillis();
	}
    
    public static long timeElapsed(long start){
        return time() - start;
    }
    
    public static String fuzzyTime(long elapsed){
        elapsed = Math.abs(elapsed / 1000);
        int fuzzy = 0;
        String label = "";
        
        if(elapsed >= 31536000){
            fuzzy = (int) elapsed / 31536000;
            label = "year";
        }else if(elapsed >= 2592000){
            fuzzy = (int) elapsed / 2592000;
            label = "month";
        }else if(elapsed >= 604800){
            fuzzy = (int) elapsed / 604800;
            label = "week";
        }else if(elapsed >= 86400){
            fuzzy = (int) elapsed / 86400;
            label = "day";
        }else if(elapsed >= 3600){
            fuzzy = (int) elapsed / 3600;
            label = "hour";
        }else if(elapsed >= 60){
            fuzzy = (int) elapsed / 86400;
            label = "minute";
        }else{
            fuzzy = (int) elapsed;
            label = "second";
        }
        
        if(fuzzy > 1){
            label += "s";
        }
        
        return fuzzy + " " + label; 
    }
}
