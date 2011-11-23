using system;

namespace SemaphoreFifo
{
    public sealed class SemaphoreFifo
	{

        private class Request
        {
            public int _required;
            public int _owned;
        }

        private readonly LinkedList<Request> _reqs;
        private readonly int _maxUnits;
        private int _currentUnits;

        public SemaphoreFifo(int units, int max)
        {
            if(max < 0 || units > max)
                throw new IllegalArgumentsException();

            _reqs = new LinkedList<Request>();
            _maxUnits = max;
            _currentUnits = units;

        }

        public bool P(int units, int timeout)
        {
            lock (_reqs)
            {
                if (units <= _currentUnits)
                {
                    _currentUnits -= units;
                    return true;
                }
                int arrivedTime = timeout != Timeout.Infinity ? Environment.TickCount : Timeout.Infinity;
                Request myreq = new Request();
                myreq._owned = _currentUnits;
                myreq._required = units;
                _currentUnits = 0;
                _reqs.AddLast(myreq);
                try
                {
                    while (true)
                    {
                        Monitor.Wait(_reqs, timeout);
                        if (myreq._owned == myreq._required)
                            return true;
                        //adjust timout
                        //arranjar o tempo de agora, subtrair a actual e obter a diferença, subtrair essa ao timout

                        if (timeout <= 0)
                        {
                            int toReturn = myreq._owned;
                            _reqs.Remove(myreq);
                            NotifyWaiters(toReturn);
                            return false;
                        }
                    }
                }
                catch (ThreadInterruptedException tie)
                {
                    if (myreq._owned == myreq._required)
                    {
                        Thread.CurrentThread.Interrupt();
                        return true;
                    }
                    int toReturn = myreq._owned;
                    _reqs.Remove(myreq);
                    NotifyWaiters(toReturn);
                    throw tie;
                }

            }
        }

        public bool P_PseudoCodigo(int units, int timeout)
        {
            lock (_reqs)
            {
                // test blocking conditions
                // get temporal reference
                // create and publish request
                try
                {
                    while (true)
                    {
                        Monitor.Wait(_reqs, timeout);
                        // test signaled and return if true
                        // adjust timeout
                        // test timeout and cleanup if true
                    }
                }
                catch (ThreadInterruptedException tie)
                {
                    // test if signaled and return if true
                    // cleanup and throw
                }
            }
        }

        public void V(int units)
        {
            lock (_reqs)
            {
                NotifyWaiters(units);
            }
        }

        public void NotifyWaiters(int units)
        {
            int count = _reqs.Count;
            while (units > 0 && _reqs.Count != 0)
            {
                Request req = _reqs.First.Value;
                int dif = req._required - req._owned;
                if (dif > units)
                {
                    req._owned += units;
                    return;
                }
                req._owned += dif;
                units -= dif;
                _reqs.RemoveFirst();
            }
            if (count != _reqs.Count) Monitor.PulseAll(_reqs);
            _currentUnits += units;
        
        }


	}
}






















































































































































































































































































































































































































































