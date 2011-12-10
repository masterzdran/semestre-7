import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public final class SemaphoreFifo
{

    private static class Request
    {
        public int _required;
        public int _owned;
        public Condition _cv;
    }

    private final LinkedList<Request> _reqs;
    private final int _maxUnits;
    private int _currentUnits;
    private final Lock _monitor;

    public SemaphoreFifo(int units, int max)
    {
        if(max < 0 || units > max)
            throw new IllegalArgumentException();

        _reqs = new LinkedList<Request>();
        _maxUnits = max;
        _currentUnits = units;
        _monitor = new ReentrantLock();

    }

    public Boolean P(int units, long timeout) throws InterruptedException
    {
        try
        {
            _monitor.lock();
        	
        	if (units <= _currentUnits)
            {
                _currentUnits -= units;
                return true;
            }
            long arrivedTime = timeout != 0 ? System.currentTimeMillis() : 0; //0 = tempo infinito
            Request myreq = new Request();
            myreq._owned = _currentUnits;
            myreq._required = units;
            _currentUnits = 0;
            myreq._cv = _monitor.newCondition();
            _reqs.addLast(myreq);
            try
            {
                while (true)
                {
                    myreq._cv.await(timeout, TimeUnit.MILLISECONDS);
                    if (myreq._owned == myreq._required)
                        return true;
                    //adjust timout
                    //arranjar o tempo de agora, subtrair a actual e obter a diferença, subtrair essa ao timout

                    if (timeout <= 0)
                    {
                        int toReturn = myreq._owned;
                        _reqs.remove(myreq);
                        NotifyWaiters(toReturn);
                        return false;
                    }
                }
            }
            catch (InterruptedException ie)
            {
                if (myreq._owned == myreq._required)
                {
                    Thread.currentThread().interrupt();
                    return true;
                }
                int toReturn = myreq._owned;
                _reqs.remove(myreq);
                NotifyWaiters(toReturn);
                throw ie;
            }

        }
        finally
        {
        	_monitor.unlock();
        }
        
    }

    public void V(int units)
    {
    	try
        {
    		_monitor.lock();
    		NotifyWaiters(units);
        }
    	finally
        {
        	_monitor.unlock();
        }
    }

    public void NotifyWaiters(int units)
    {
        while (units > 0 && _reqs.size() != 0)
        {
            Request req = _reqs.getFirst();
            int dif = req._required - req._owned;
            if (dif > units)
            {
                req._owned += units;
                return;
            }
            req._owned += dif;
            units -= dif;
            _reqs.removeFirst();
	        req._cv.signalAll(); //satisfaz apenas a própria thread
        }
        _currentUnits += units;
    
    }


}