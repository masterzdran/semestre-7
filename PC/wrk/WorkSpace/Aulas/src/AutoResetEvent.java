import java.util.LinkedList;

public final class AutoResetEvent {
	
	private boolean _state;
	private final LinkedList<ThreadRequest> _request;
	
	private static class ThreadRequest
	{
		public boolean _signaled;	
	}
	
	public AutoResetEvent()
	{
		_state = false;
		_request = new LinkedList<ThreadRequest>();
	}
		
	public void Set()
	{
		synchronized (_request) {
			if (_request.isEmpty())
				_state = true;
			else
			{
				ThreadRequest tr = _request.removeFirst();
				tr._signaled = true;
				_request.notifyAll();
			}
		}
	}
	
	public void Reset()
	{
		synchronized (_request) {
			_state = false;
		}
	}
	
	public void WaitOne() throws InterruptedException
	{
		synchronized (_request) {
			if(_state)
			{
				_state = false;
				return;
			}
			ThreadRequest tr = new ThreadRequest();
			_request.addLast(tr);
			try {
				while (true)
				{
					_request.wait();
					if(tr._signaled) return;
				}
			}
			catch (InterruptedException ie) {
				if (tr._signaled)
				{
					Thread.currentThread().interrupt();
					return;
				}
				_request.remove(tr);
				throw ie;
			}
		}
	}

}
