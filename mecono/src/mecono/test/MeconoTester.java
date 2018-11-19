package mecono.test;

public class MeconoTester {
	public void test(){
		System.out.println("Running tests...");
		Test test;
		
		test = new TestNode();
		test.test();
		
		test = new TestParcel();
		test.test();
		
		System.out.println("All tests passed");
	}
}