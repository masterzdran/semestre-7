
public class ManualResetEventWithTransitionCheck {

	private final Object _mre;
	private boolean _state;
	private int _transitionCount;
	
	public ManualResetEventWithTransitionCheck()
	{
		_mre = new Object();
		_state = false;
		_transitionCount = 0;
	}
	
	public void Set()
	{
		synchronized (_mre) {
			if (!_state) ++_transitionCount;
			_state = true;
			_mre.notifyAll();
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
				if(_state)
					return;
				int count = _transitionCount;
				_mre.wait();
				if (count != _transitionCount)
					return; //houve uma transacção, fui sinalizado, posso retornar.
			}
		}
	}
}