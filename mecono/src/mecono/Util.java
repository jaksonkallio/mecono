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
            label = "y";
        }else if(elapsed >= 2592000){
            fuzzy = (int) elapsed / 2592000;
            label = "mo";
        }else if(elapsed >= 604800){
            fuzzy = (int) elapsed / 604800;
            label = "w";
        }else if(elapsed >= 86400){
            fuzzy = (int) elapsed / 86400;
            label = "d";
        }else if(elapsed >= 3600){
            fuzzy = (int) elapsed / 3600;
            label = "h";
        }else if(elapsed >= 60){
            fuzzy = (int) elapsed / 86400;
            label = "m";
        }else{
            fuzzy = (int) elapsed;
            label = "s";
        }
        
        /*if(fuzzy > 1){
            label += "s";
        }*/
        
        return fuzzy + label; 
    }
    
    public static String bytesToHex(byte[] bytes) {
        // https://stackoverflow.com/a/9855338
        
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        
        return new String(hexChars);
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
}
