using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Ficha1
{
    public class Exchanger<T>
    {
        private ThreadMessageRequest _threadMessageRequest;

        class ThreadMessageRequest 
        {
            public T _message;

            public ThreadMessageRequest(T message)
            {
                _message = message;
            }
        }

        public Exchanger()
        {
            _threadMessageRequest = null;
        }

        
        //TODO timeout; excepção interrupção; 

        public bool Exchange(T mine, int timeout, out T yours)
        {
            lock (_threadMessageRequest)
            {
                if (_threadMessageRequest == null)
                    //first thread to arrive
                {
                    ThreadMessageRequest MyRequest = new ThreadMessageRequest(mine);
                    _threadMessageRequest = MyRequest;
                    try
                    {
                        Monitor.Wait(_threadMessageRequest, timeout);
                        //if message was delivered "at the same time" than the timeout gets answer and returns with true
                        if (_threadMessageRequest == null)
                        {
                            yours = MyRequest._message;
                            return true;
                        }
                        //remove request from queue
                        _threadMessageRequest = null;
                        yours = default(T);
                        return false;
                    }
                    catch (Exception)
                    {
                        if (_threadMessageRequest == null)
                        {
                            yours = MyRequest._message;
                            Thread.CurrentThread.Interrupt();
                            return true;
                        }
                        _threadMessageRequest = null;
                        yours = default(T);
                        Thread.CurrentThread.Interrupt();
                        return false;
                    }
                }
                else
                    //some thread already requested a message so exchange them
                {
                    yours = _threadMessageRequest._message;
                    _threadMessageRequest._message = mine;
                    //removes request from queue
                    _threadMessageRequest = null;
                    Monitor.Pulse(_threadMessageRequest);
                    return true;
                }
            }
        }
    }
}
