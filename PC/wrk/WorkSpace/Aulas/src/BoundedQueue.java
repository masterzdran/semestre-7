import java.util.LinkedList;

public class BoundedQueue<T> {
	
	private final LinkedList<T> _buffer;
	private final LinkedList<TakeRequest<T>> _requests;
	private final int _capacity;

	private class TakeRequest<T>
	{
		public T _element;	
	}

	public BoundedQueue(int capacity)
	{
		_buffer = new LinkedList<T>();
		_requests = new LinkedList<TakeRequest<T>>();
		_capacity = capacity;
	}

	public void put (T elem) throws InterruptedException
	{
		synchronized (_buffer) {
			if(_buffer.size() != _capacity)
			{
				if (_requests.isEmpty())
				{
					_buffer.addLast(elem);
					return;
				}
				TakeRequest<T> tr = _requests.removeFirst();
				tr._element = elem;
				_buffer.notifyAll();
				return;
			}
			TakeRequest<T> tr = new TakeRequest<T>();
			tr._element = elem;
			_requests.addLast(tr);
			try{
				while (true)
				{
					_buffer.wait();
					if(tr._element == null) return;
				}	
			}
			catch (InterruptedException ie)
			{
				//em java este if nunca dá true, não há sinalização e cancelamento em simultâneo
				if(tr._element == null)
				{
					Thread.currentThread().interrupt();
					return;
				}
				_requests.remove(tr);
				throw ie;
			}
		}		
	}

	public T take() throws InterruptedException
	{
		synchronized (_buffer) {
			if(!_buffer.isEmpty())
			{
				T elem = _buffer.removeFirst();
				if (!_requests.isEmpty())
				{
					TakeRequest<T> tr = _requests.removeFirst();
					_buffer.addLast(tr._element);
					tr._element = null;
					_buffer.notifyAll();
				}
				return elem;
			}
			TakeRequest<T> tr = new TakeRequest<T>();
			_requests.addLast(tr);
			try{
				while (true)
				{
					_buffer.wait();
					if(tr._element != null) return tr._element;
				}	
			}
			catch (InterruptedException ie)
			{
				if(tr._element != null)
				{
					Thread.currentThread().interrupt();
					return tr._element;
				}
				_requests.remove(tr);
				throw ie;
			}
		}
	}
}
