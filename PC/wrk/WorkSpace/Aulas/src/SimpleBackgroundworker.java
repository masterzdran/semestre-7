import java.awt.EventQueue;


public abstract class SimpleBackgroundworker {
	
	private Object _result;
	private final Object _lock;

	public abstract Object doInBackground() throws Exception;
	
	public abstract void done();
	
	public SimpleBackgroundworker()
	{
		_result = new Object();
		_lock = new Object();
	}
	
	public final void execute()
	{
		new Thread(new Runnable() {
			public void run() {
				try {
						Object result = doInBackground();
						synchronized (_lock) {
							_result = result;
						}
					} catch (Exception e) { 
						synchronized (_lock) {
							_result = e;
							}
					}
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						done();	
					}
				});
			}
		}).start();
	}
	
	public final Object get() throws Exception
	{
		/*
		 synchronized (_lock) {
			if (_result instanceof Exception)
				throw (Exception) _result;
			return _result;
		}
		*/
		
		while(true){
			synchronized (_lock) {
				if (_result instanceof Exception)
					throw (Exception) _result;
				return _result;
			}
		}
		
	}
	
	public final boolean isDone()
	{
		synchronized(_lock)
		{
			return !(_result == null);
		}
	}
	
	
}
