import java.util.ArrayList;


public final class BoundedBuffer<T> {
	
	private final ArrayList<T> _buffer;
	private final int _capacity;
	
	public BoundedBuffer (int capacity){
		_capacity = capacity;
		_buffer = new ArrayList<T>(capacity);
	}
	
	
	public void put (T elem) throws InterruptedException
	{
		synchronized (_buffer) {
			while (true){
				if(_buffer.size() != _capacity){
					_buffer.add(elem);
					_buffer.notifyAll();
					return;
				}
				_buffer.wait();
			}
		}
	}
	
	
	public T take() throws InterruptedException
	{
		synchronized (_buffer) {
			while (true){
				if(_buffer.size() != 0){
					T elem = _buffer.remove();
					_buffer.notifyAll();
					return elem;
				}
				_buffer.wait();
			}
		}
		
	}
	

}
