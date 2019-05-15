package node;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import mecono.Self;
import mecono.Util;
import static parcel.Trigger.PARCEL_ID_LEN;

public class Connection {
	public Connection(MNode n1, MNode n2){
		nodes = new HashSet<>();
		nodes.add(n1);
		nodes.add(n2);
		ping_samples = new LinkedBlockingQueue<>();
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Connection){
			Connection other = (Connection) o;
			
			for(MNode other_node : other.getNodes()){
				if(!getNodes().contains(other_node)){
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public MNode getOther(MNode source){
		for(MNode other : getNodes()){
			if(!other.equals(source)){
				return other;
			}
		}
		
		return null;
	}
	
	public double reliability(){
		return successes / Math.max(1, total);
	}
	
	public Set<MNode> getNodes(){
		return nodes;
	}
    
    public void logSuccess(long ping){
		logPing(ping);
        successes++;
    }
	
	public void logPing(long ping){
		ping_samples.offer(ping);
		
		while(ping_samples.size() > PING_SAMPLE_SIZE){
			ping_samples.remove();
		}
	}
	
	public long getPing(){
		long sum = 0;
		
		for(Long ping : ping_samples){
			sum += ping;
		}
		
		return (sum / ping_samples.size());
	}
	
	public boolean online(){
		return Util.timeElapsed(last_use) < ONLINE_THRESHOLD;
	}
    
	public long elapsedLastUse(){
		return Util.timeElapsed(last_use);
	}
	
    public void logUse(){
		last_use = Self.time();
        total++;
    }
	
	public static final long ONLINE_THRESHOLD = 120000;
	public static final int PING_SAMPLE_SIZE = 20;
	
	private final Set<MNode> nodes;
	private String id;
	private int successes;
	private int total;
	private long last_use;
	private Queue<Long> ping_samples;
}
