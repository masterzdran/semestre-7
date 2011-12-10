using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace ConcurrentProgramming
{

    sealed class Exchange<T>
    {
        sealed class EntityWorker { public T thread; public Boolean isDone;}
        readonly EntityWorker _exchanger;
        public Exchange()
        {
            _exchanger = new EntityWorker();
            _exchanger.isDone = true;
        }

        public bool Exchange(T mine, int timeout, out T yours)
        {
            lock (_exchanger)
            {
                while (true)
                {
                    if (!object.Equals(_exchanger.thread, default(T)) && _exchanger.isDone)
                    {
                        try
                        { Monitor.Wait(_exchanger); }
                        catch (Exception)
                        {
                            //Somethingto do
                            throw;
                        }
                    }
                    else { break; }
                }
                if (object.Equals(_exchanger.thread, default(T)))
                {
                    _exchanger.thread = mine;
                    _exchanger.isDone = false;

                    int endTime = Environment.TickCount + timeout;
                    try
                    {
                        Monitor.Wait(_exchanger, timeout);
                        yours = _exchanger.thread;
                        _exchanger.thread = default(T);
                        _exchanger.isDone = false;

                        if (Environment.TickCount > endTime)// ocorreu timeout
                        { return false; }
                        return true;
                    }
                    catch (Exception)
                    {
                        yours = _exchanger.thread;
                        _exchanger.thread = default(T);
                        return false;
                    }
                }
                else
                {
                    yours = _exchanger.thread;
                    _exchanger.thread = mine;
                    _exchanger.isDone = true;
                    Monitor.PulseAll(_exchanger);
                    return true;
                }
            }
        }
    }
}