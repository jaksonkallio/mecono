package mecono.test;

public abstract class Test {
	public abstract void test();
	
	public final void assertEqual(Object o1, Object o2){
		int test_id = getTestID();
		boolean success = true;
		StringBuilder str = new StringBuilder();
		str.append("Test #"+test_id+": ");
		
		if(o1.equals(o2)){
			str.append("PASS");
		}else{
			success = false;
			str.append("FAIL");
		}
		
		System.out.println(str);
		
		if(!success){
			System.exit(0);
		}
	}
	
	public static int getTestID(){
		test_id++;
		return test_id;
	}
	
	public static int test_id = 0;
}