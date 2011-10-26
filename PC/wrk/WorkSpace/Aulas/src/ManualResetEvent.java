
public class ManualResetEvent {

	private final Object _mre;
	private boolean _state;
	
	public ManualResetEvent()
	{
		_mre = new Object();
		_state = false;
	}
	
	public void Set()
	{
		synchronized (_mre) {
			_state = true;
		}
	}
	
	public void Reset()
	{
		synchronized (_mre) {
			_state = false;
		}
	}
	
	public void WaitOne() throws InterruptedException
	{
		synchronized (_mre) {
			while (true)
			{
				if(_state == true){
					_mre.notifyAll();
					return;
				}
				_mre.wait();
			}
		}
	}
	
}