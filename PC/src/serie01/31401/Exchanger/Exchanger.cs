using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Serie01
{
    sealed class Exchanger<T>
    {
        class EntityWorker {public T thread1;}

        sealed EntityWorker _exchanger;

        public Exchanger() 
        { 
            _exchanger = new EntityWorker();
        }

        public bool Exchange(T mine, int timeout, out T yours)
        {
            lock (_exchanger) 
            {
                if (object.Equals(_exchanger.thread1,default(T)))
                {
                    int endTime = Environment.TickCount+timeout ;
                    _exchanger.thread1 = mine;

                    try
                    {
                            Monitor.Wait(_exchanger, timeout);
                            yours = _exchanger.thread1;
                            _exchanger.thread1 = default(T);

                            if (Environment.TickCount > endTime)
                            {// ocorreu timeout
                                return false;
                            }
                            return true;
                    }
                    catch (Exception)
                    {
                        yours = _exchanger.thread1;
                        _exchanger.thread1 = default(T);
                        return false;
                    }
                }
                else
                {
                    yours = _exchanger.thread1;
                    _exchanger.thread1 = mine;
                    Monitor.Pulse(_exchanger);
                    return true;
                }
            }
        }


    }
}
