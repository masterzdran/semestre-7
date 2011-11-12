import java.util.LinkedList;

public class BoundedQueue<T> {
	
	private final LinkedList<T> _buffer;
	private final LinkedList<TakeRequest<T>> _requests;
	
	private class TakeRequest<T>
	{
		public T _element;	
	}
	
	public BoundedQueue()
	{
		_buffer = new LinkedList<TakeRequest<T>>();
		
	}
	
	
	public void put (T elem) throws InterruptedException
	{
		
		
		
	}
	
	
	
	public T take() throws InterruptedException
	{
		
		
		
	}
	

}
