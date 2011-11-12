import java.util.LinkedList;

public class UnboundedQueue<T> {
	
	private final LinkedList<T> _buffer;
	private final LinkedList<TakeRequest<T>> _requests;
	
	public UnboundedQueue (){
		_buffer = new LinkedList<T>();
		_requests = new LinkedList<TakeRequest<T>>();
	}
	
	private class TakeRequest<T>
	{
		public T _element;	
	}
	
	public void put (T elem) throws InterruptedException
	{
		synchronized (_buffer) {
			if(!_requests.isEmpty()){
				TakeRequest<T> req = _requests.removeFirst();
				req._element = elem;
				_buffer.notifyAll();
				return;
			}
			_buffer.addLast(elem);
		}
	}
	
	public T take() throws InterruptedException
	{
		synchronized (_buffer) {
			if(_buffer.size() != 0){
				return _buffer.removeFirst();
			}
			TakeRequest<T> myRequest = new TakeRequest<T>();
			_requests.addLast(myRequest);
			while (true){
				/*_buffer.wait();
				if (myRequest._element != null)
					return myRequest._element;*/
				try{_buffer.wait();}
				catch (InterruptedException ie) {
					if (myRequest._element != null)
					{
						Thread.currentThread().interrupt();
						return myRequest._element;
					}
					_requests.remove(myRequest);
					throw ie;
				}
				if (myRequest._element != null)
					return myRequest._element;
			}
		}
	}
}
