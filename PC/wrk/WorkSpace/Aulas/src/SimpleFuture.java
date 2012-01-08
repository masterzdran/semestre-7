import java.util.concurrent.Callable;

public class SimpleFuture<T> {
	
	private final CountDownLatch _latch;
	private final Callable<T> _func;
	private volatile T _result;
	
	public SimpleFuture (Callable<T> func)
	{
		_latch = new CountDownLatch(1);
		_func = func;
		_result = null;
		(new Thread(){
			public void run()
			{
				try {
					setResult(_func.call());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void setResult(T result)
	{
		_result = result;
		_latch.CountDown();
	}
	
	public T getResult()
	{
		try {
			_latch.Await();
		} catch (InterruptedException ie) {
			// TODO Auto-generated catch block
			ie.printStackTrace();
		}
		return _result;
	}

}
