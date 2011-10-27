
public class MainTests {


		
	public static void LatchTest() throws InterruptedException
	{
		final int COUNT=10;
		final CountDownLatch Latch = new CountDownLatch(COUNT);
		
		for (int i=0; i<COUNT; ++i)
		{
			new Thread(new Runnable() {
				public void run(){
					Latch.CountDown();
				}
				}).start();
			
		}
		Latch.Await();
	}
	
	
	public static void main(String[] args) throws InterruptedException
	{
		
		LatchTest();
		
	}
	
	
	
}
