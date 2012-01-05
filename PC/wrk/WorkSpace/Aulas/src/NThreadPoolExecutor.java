import java.util.LinkedList;
import java.util.concurrent.Executor;

public final class NThreadPoolExecutor implements Executor {

	private LinkedList<Runnable> _queue;
	
	//public NThreadPoolExecutor(int initialThreads, int maxThreads) throws InterruptedException
	public NThreadPoolExecutor(int nThreads) throws InterruptedException
	{
		_queue = new LinkedList<Runnable>();
		while (nThreads-- != 0)
		{
			(new Thread() {
				public void run() {
					try {
						threadAction();
					} catch (InterruptedException _) {
						//protecção para não perder a thread
					}					
				}
			}).start();
		}
	}
	
	public void execute(Runnable action)
	{
		synchronized (_queue)
		{
			_queue.addLast(action);
			
		}
	}
	
	public void shutdown()
	{
		
	}
	
	public void threadAction() throws InterruptedException
	{
		while(true)
		{
			Runnable action = null;
			synchronized (_queue)
			{
				if(!_queue.isEmpty())
					action = _queue.remove();
				else
					_queue.wait();
			}
			if(action != null)
				try{ action.run(); } catch (Throwable _){}
		}
	}
	
}
