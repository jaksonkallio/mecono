package node;

public class GeoCoord {
	public GeoCoord(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public double dist(GeoCoord o){
		return Math.sqrt(Math.pow(this.x - o.x, 2) + Math.pow(this.y - o.y, 2));
	}
	
	public final int x;
	public final int y;
}
