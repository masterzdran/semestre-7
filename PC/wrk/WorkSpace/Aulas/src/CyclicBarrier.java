import java.util.concurrent.BrokenBarrierException;

public final class CyclicBarrier {
	
	private final int _participants;
	private int _order;
	private final Object _cblock;
	private final Runnable _action;
	private Request _requests;
	
	public class Request
	{
		boolean _isBroken;
		boolean _isSignaled;
	}
	
	public CyclicBarrier(int participants)
	{
		this(participants, null);
	}
	
	public CyclicBarrier(int participants, Runnable action)
	{
		if (participants < 0) throw new IllegalArgumentException();
		_participants = participants;
		_cblock = new Object();
		_order = 0;
		_action = action;
	}
	
	public void Reset()
	{
		synchronized (_cblock) {
			NotifyWaiters(false);
		}
	}
	
	public int Await() throws InterruptedException, BrokenBarrierException
	{
		synchronized (_cblock) {
			if (++_order == _participants)
			{
				NotifyWaiters(true); //porreiro vamos sinaliza-los
				if (_action != null) _action.run(); //é executado quando a barreira "abre"
				return _participants;
			}
			final int order = ++_order;
			Request myreq = _requests;
			try
			{
				while(true)
				{	
					_cblock.wait();
					if (myreq._isSignaled) return order;
					if (myreq._isBroken) throw new BrokenBarrierException();
				}
			}
			catch (InterruptedException ie)
			{
				NotifyWaiters(false);				
				throw ie;
			}
		}
	}
	
	public void NotifyWaiters(boolean signaled)
	{
		if (signaled)
			_requests._isSignaled = true;
		else
			_requests._isBroken = true;
		_requests = new Request();
		_order = 0;
		_cblock.notifyAll();
	}

}
