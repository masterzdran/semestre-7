using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Ficha1
{
    public class Exchanger<T>
    {
        private readonly ThreadMessage _threadMessage;
        private bool _fulfilled;

        class ThreadMessage 
        {
            public T _message;
        }

        public Exchanger()
        {
            _fulfilled = false;
            _threadMessage = new ThreadMessage();
        }

        
        //TODO timeout; excepção interrupção; 

        public bool Exchange(T mine, int timeout, out T yours)
        {
            lock (_threadMessage)
            {
                if (_threadMessage._message.Equals(default(T)))
                    //first thread to arrive
                {
                    _threadMessage._message = mine;
                    try
                    {
                        bool timedOut = Monitor.Wait(_threadMessage, timeout);
                        if (timedOut)
                        {
                            //check if the 2nd message was already there when timed out
                            //if it was it has to take it anyway
                            yours = default(T);
                            if (!_fulfilled)
                            {
                                _threadMessage._message = default(T);
                                return false;
                            }
                            yours = _threadMessage._message;
                            _threadMessage._message = default(T);
                            _fulfilled = false;
                            return true;
                        }
                        else
                        {
                            //if not timedout then was pulsed and message is from other thread
                            yours = _threadMessage._message;
                            _threadMessage._message = default(T);
                            _fulfilled = false;
                            return true;
                        }
                    }
                    catch (Exception)
                    {
                        if(!_fulfilled)
                        {
                            _threadMessage._message = default(T);
                            yours = default(T);
                            Thread.CurrentThread.Interrupt();
                            return false;
                        }
                        yours = _threadMessage._message;
                        _threadMessage._message = default(T);
                        _fulfilled = false;
                        Thread.CurrentThread.Interrupt();
                        return true;
                    }
                }
                else if(!_fulfilled)
                {
                    yours = _threadMessage._message;
                    _threadMessage._message = mine;
                    _fulfilled=true;
                    Monitor.Pulse(_threadMessage);
                    return true;
                }
                return false;
            }
        }
    }
}
