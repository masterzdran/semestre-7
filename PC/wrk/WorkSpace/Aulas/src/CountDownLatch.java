
public class CountDownLatch {
	
	private int _counter;
	private final Object _latch;
	
	public CountDownLatch(int count)
	{
		if (_counter < 0) throw new IllegalArgumentException();
		_counter = count;
		_latch = new Object();
	}
	
	public void CountDown()
	{
		synchronized (_latch) {
			if (_counter <= 0) throw new IllegalStateException();
			_counter--;
			if (_counter == 0 ) _latch.notifyAll();
		}
		
	}
	
	public void Await() throws InterruptedException
	{
		synchronized (_latch) {
			while (true)
			{
				if(_counter == 0)
					return;
				_latch.wait();
			}
		}
	}

}
