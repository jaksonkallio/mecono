package mecono.test;

import mecono.node.SelfNode;

public class TestNode extends Test {
	public TestNode(){
		test_self = new SelfNode();
	}
	
	@Override
	public void test(){
		
	}
	
	private final SelfNode test_self;
}