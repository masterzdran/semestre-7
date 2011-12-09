using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Ficha1
{
    class RendezvousChannel<S,R>
    {
     
        private readonly List<Request> _requests;
        private readonly List<Request> _inProcess;
        private readonly object _myMonitor;

        class Request
        {
            public S _service;
            public R _response;
        }

        
        public RendezvousChannel()
        {
            _requests = new List<Request>();
            _inProcess = new List<Request>();
            _myMonitor = new object();
        }

        private R waitForResponse(object _lock, Request req)
        {
            while (true)
            {
                try
                {
                    if (!_inProcess.Contains(req))
                    {
                        Monitor.Exit(_lock);
                        return req._response;
                    }
                    Monitor.Wait(_lock);

                }catch(ThreadInterruptedException e){
                    if (!_inProcess.Contains(req))
                    {
                        Thread.CurrentThread.Interrupt();
                        return req._response;
                    }
                }
                finally
                {
                    Monitor.Exit(_lock);    
                }
            }
        }

        public bool Request(S service, int timeout, out R response) 
        {
            response = default(R);
            
            Monitor.Enter(_myMonitor);
            if (_requests.Count != 0)
            {
                //there are server threads waitting
                Request req = _requests.First();
                req._service = service;
                _requests.Remove(req);
                Monitor.PulseAll(_myMonitor);
                //thread already processing so this thread can't quit
                //drop main monitor, enter inProcess monitor and waits for response
                Monitor.Enter(_inProcess);
                _inProcess.Add(req);
                Monitor.Exit(_myMonitor);
                response = waitForResponse(_inProcess, req);
                return true;
            } else {
                Request myReq = new Request();
                myReq._service = service;
                _requests.Add(myReq);
                try
                {
                    bool timedOut = Monitor.Wait(_myMonitor, timeout);
                    //if timeout before server thread started processing
                    //quits waiting and exits
                    if (timedOut && _requests.Contains(myReq))
                    {
                        _requests.Remove(myReq);
                        Monitor.Exit(_myMonitor);
                        return false;
                    }
                    Monitor.Enter(_inProcess);
                    Monitor.Exit(_myMonitor);
                    response = waitForResponse(_inProcess, myReq);
                }catch(ThreadInterruptedException ie){
                    if (_requests.Contains(myReq))                
                    {
                        _requests.Remove(myReq);
                        Thread.CurrentThread.Interrupt();
                        return false;
                    }
                }
                finally
                {
                    Monitor.Exit(_myMonitor);
                }
                return true;
            }

        }

        public Object Accept(int timeout, out S service)
        { 
            service = default(S);
            Monitor.Enter(_myMonitor);
            if (_requests.Count != 0)
            {
                //there are client threads waitting
                Request req = _requests.First();         
                _requests.Remove(req);
                Monitor.PulseAll(_myMonitor);
                _inProcess.Add(req);
                service = req._service;
                Monitor.Exit(_myMonitor);
                return req;
            }
            else
            {
                Request myReq = new Request();
                _requests.Add(myReq);
                try
                {
                    bool timedOut = Monitor.Wait(_myMonitor, timeout);
                    //it timed out before get out without processing
                    if (timedOut && _requests.Contains(myReq))
                    {
                        _requests.Remove(myReq);
                        Monitor.Exit(_myMonitor);
                        return null;
                    }
                    service = myReq._service;
                    return myReq;

                }catch(ThreadInterruptedException ie){
                    if (_requests.Contains(myReq))                
                    {
                        _requests.Remove(myReq);
                        Thread.CurrentThread.Interrupt();
                        return null;
                    }
                }
                finally
                {
                    Monitor.Exit(_myMonitor);
                }
                return true;
            }
        }

        public void Reply(object rendezVousToken, R response)
        {
            Monitor.Enter(_inProcess);
            int idx = _inProcess.IndexOf((Request)(rendezVousToken));
            Request req = _inProcess.ElementAt(idx);
            req._response = response;
            Monitor.PulseAll(_inProcess);
            Monitor.Exit(_inProcess);
        }
    }
}
