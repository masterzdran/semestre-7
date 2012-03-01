using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.Threading;

namespace Tracker
{
    public sealed class Listener
    {
        private readonly int portNumber;
        private readonly Logger _log;
        private volatile bool _IsInShutDownMode=false;
        private int _NbrActiveConnections = 0;
        public static ManualResetEvent clientConnected = new ManualResetEvent(false);

        public Listener(int port, Logger log) { portNumber = port; _log = log;}
        public Listener(int port):this(port,new Logger()) {}
        
        
        public void Run()
        {
            TcpListener srv = null;
            clientConnected.Reset();
            try
            {
                srv = new TcpListener(IPAddress.Loopback, portNumber);
                srv.Start();

                srv.BeginAcceptTcpClient(new AsyncCallback(AsyncActionCallBack), srv);
                lock (_log)
                {
                    _log.LogMessage("Listener - Waiting for connection requests.");
                }
                clientConnected.WaitOne();
            }
            catch (SocketException e)
            {
                _log.LogMessage(String.Format("Listener Socket Exception :: {0}", e.Message));
            }
            finally
            {
                _IsInShutDownMode = true;
                lock (clientConnected)
                {
                    if (_NbrActiveConnections > 0)
                    {
                        clientConnected.WaitOne();
                    }
                }
                _log.LogMessage("Listener - Ending.");
                srv.Stop();
            }
        }

        void AsyncActionCallBack(IAsyncResult ar)
        {
            TcpClient socket = null;
            try
            {
                TcpListener tcp = (TcpListener)ar.AsyncState;
                Socket clientSocket = tcp.EndAcceptSocket(ar);
                clientSocket.ReceiveTimeout = 2000;
                using (socket = tcp.AcceptTcpClient())
                {
                    _log.LogMessage(String.Format("Listener - Connection established with {0}.", socket.Client.RemoteEndPoint));

                    int nbrCon = Interlocked.Increment(ref _NbrActiveConnections);
                    if (!_IsInShutDownMode)
                        tcp.BeginAcceptSocket(new AsyncCallback(AsyncActionCallBack), tcp);
                    socket.LingerState = new LingerOption(true, 10);
                    Handler protocolHandler = new Handler(socket.GetStream(), _log);
                    protocolHandler.Run();
                    Program.ShowInfo(Store.Instance);
                    nbrCon = Interlocked.Decrement(ref _NbrActiveConnections);
                    clientConnected.Set();
                }

            }
            catch (SocketException e)
            {
                _log.LogMessage(String.Format("Listener Socket Exception :: {0}", e.Message));
            }
            finally {
                if (socket != null)
                {
                    socket.Close();
                }
            }

        }

    }
}
