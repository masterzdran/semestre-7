// ManualResetEventCDE => Com Delegação de Execução
public class ManualResetEventCDE {

	private boolean _state;
	private ThreadRequest _mrecde;
	private final Object _monitor;
	
	public static class ThreadRequest
	{
		public boolean _signaled;
		public int _counter;
	}
	
	public ManualResetEventCDE()
	{
		_state = false;
		_mrecde = null;
		_monitor = new Object();
	}
	
	public void Set()
	{
		synchronized (_monitor) {
			_state = true;
			if (_mrecde == null)
				return;
			_mrecde._signaled = true;
			_mrecde = null;
			_monitor.notifyAll();		
		}
	}
	
	public void Reset()
	{
		synchronized (_monitor) {
			_state = false;
		}
	}
	
	public boolean WaitOne(long timeout) throws InterruptedException
	{
		synchronized (_monitor) {
			if(_state) return true;
			ThreadRequest mrecde = (_mrecde == null) ? _mrecde = new ThreadRequest() : _mrecde;
			mrecde._counter += 1;
			long lastTime = timeout == 0 ? 0: System.currentTimeMillis();
			try {
				while (true)
				{
					_monitor.wait(timeout);
					if(mrecde._signaled) return true;
					if (timeout != 0)
					{
						long now = System.currentTimeMillis();
						long elapsed = now - lastTime;
						if (elapsed >= timeout) return false;
						lastTime = now;
						timeout -= elapsed;
					}
				}
			}
			catch (InterruptedException ie) {
				if (_mrecde == mrecde && --mrecde._counter == 0)
				{
					_mrecde = null;
				}
				throw ie;
			}

		}
		
	}
	
	
}
