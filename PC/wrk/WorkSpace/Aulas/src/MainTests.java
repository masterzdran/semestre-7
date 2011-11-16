import java.util.concurrent.BrokenBarrierException;


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
	
	
	public static void CyclicBarrierTest(int participants) throws InterruptedException, BrokenBarrierException
	{
		final CyclicBarrier cb = new CyclicBarrier(participants);
		/*
		for (int i=1; i<=participants; ++i)
		{
			new Thread(new Runnable() {
				public void run(){
					//cb.CountDown();
				}
				}).start();	
		}
		*/
		cb.Await();
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		
		LatchTest();
		
	}
	
	
	
}
