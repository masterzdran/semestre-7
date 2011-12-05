using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace RendezvousChannel
{
    class RendezvousChannel<S, R>
    {
        sealed class RequestItem<S, R>
        {
            public RequestItem(S service, R request)
            {
                this.Request = service;
                this.Response = request;
                this.State = false;
            }
            public S Request { get; set; }
            public R Response { get; set; }
            public Boolean State { get; set; }
        }

        readonly LinkedList<RequestItem<S, R>> _clientRequest;
        readonly LinkedList<Object> _serverRequest;

        public RendezvousChannel()
        {
            _clientRequest = new LinkedList<RequestItem<S, R>>();
            _serverRequest = new LinkedList<object>();
        }

        public bool Request(S service, int timeout, out  R response)
        {
            lock (_serverRequest)
            {
                RequestItem<S, R> request = new RequestItem<S, R>(service, default(R));
                LinkedListNode<RequestItem<S, R>> requestNode = _clientRequest.AddLast(request);

                if (_serverRequest.Count > 0)
                {
                    SyncUtils.Notify(_serverRequest, _serverRequest.First);
                }
                int endtime = (timeout > 0) ? timeout + Environment.TickCount : 0;
                while (true)
                {
                    try
                    {
                        if (!requestNode.Value.State)
                        {
                            SyncUtils.Wait(_serverRequest, requestNode, timeout);
                        }
                    }
                    catch (ThreadInterruptedException)
                    {
                        response = requestNode.Value.Response;
                        if (requestNode.Value.State)
                        {
                            return false;
                        }
                        else
                        {
                            _clientRequest.Remove(requestNode);
                            Thread.CurrentThread.Interrupt();
                        }
                        throw;
                    }
                    
                    if (requestNode.Value.State)
                    {
                        _clientRequest.Remove(requestNode);
                        response = requestNode.Value.Response;
                        return true;
                    }
                    if (Environment.TickCount > endtime && endtime != 0)
                    {
                        _clientRequest.Remove(requestNode);
                        response = default(R);
                        return false;
                    }
                }
            }
        }


        public object Accept(int timeout, out S service)
        {
            lock (_serverRequest)
            {
                RequestItem<S, R> request = null;
                Object serverThread = new Object();
                int endTime = (timeout > 0) ? timeout + Environment.TickCount : 0;
                _serverRequest.AddLast(serverThread);

                while (true)
                {
                    try
                    {
                        if (_clientRequest.Count > 0)
                        {
                            request = _clientRequest.First.Value;
                            _clientRequest.RemoveFirst();
                            service = request.Request;
                            _serverRequest.Remove(serverThread);
                            return request;
                        }
                        else
                        {
                            SyncUtils.Wait(_serverRequest, serverThread, timeout);
                        }
                    }
                    catch (Exception)
                    {
                        _serverRequest.Remove(serverThread);
                        if (request == null)
                            throw;

                        service = request.Request;
                        _clientRequest.Remove(request);
                        Thread.CurrentThread.Interrupt();
                        throw;
                    }
                    _serverRequest.Remove(serverThread);
                    if (Environment.TickCount > endTime && endTime != 0)
                    {
                        service = default(S);
                        return false;
                    }
                }
            }
        }

        public void Reply(object rendezVousToken, R response)
        {
            RequestItem<S, R> request = rendezVousToken as RequestItem<S, R>;
            request.Response = response;
            SyncUtils.Notify(_serverRequest, request.Response);
        }
    }
}
