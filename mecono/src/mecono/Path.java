package mecono;

import java.util.ArrayList;

/**
 *
 * @author jak
 */
public class Path {
	
	public Path(ArrayList<PathSegment> segments) {
		this.segments = segments;
	}
	
	public Path(String[] addresses) {
		for (String address : addresses) {
			segments.add(new PathSegment(address, this));
		}
	}
	
    private ArrayList<PathSegment> segments;
}