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
                while (!_IsInShutDownMode)
                {
                    srv = new TcpListener(IPAddress.Loopback, portNumber);
                    srv.Start();

                    srv.BeginAcceptTcpClient(new AsyncCallback(AsyncActionCallBack), srv);
                    lock (clientConnected)
                    {
                        _log.LogMessage("Listener - Waiting for connection requests.");
                    }
                    clientConnected.WaitOne();
                }
            }
            catch (SocketException e)
            {
                _log.LogMessage(String.Format("Listener Socket Exception :: {0}", e.Message));
            }
            finally
            {
                lock (clientConnected)
                {
                    _IsInShutDownMode = true; //por causa da excepção
                    while(_NbrActiveConnections > 0)
                        clientConnected.WaitOne();
                }
                _log.LogMessage("Listener - Ending.");
                srv.Stop();
            }
        }

        public void Shutdown()
        {
            lock (clientConnected) { _IsInShutDownMode = true; }
        }

        void ActionCallBack(Socket socket,Logger log)
        {
            int nbrCon = 0;
            lock (clientConnected)
            {
                nbrCon = Interlocked.Increment(ref _NbrActiveConnections);
            }
            Handler protocolHandler = new Handler(new NetworkStream(socket), log);
            protocolHandler.Run();
            Program.ShowInfo(Store.Instance);

            lock (clientConnected)
            {
                nbrCon = Interlocked.Decrement(ref _NbrActiveConnections);
                if (_IsInShutDownMode && _NbrActiveConnections == 0)
                    clientConnected.Set();
            }
            

        }
        void AsyncActionCallBack(IAsyncResult ar)
        {
            TcpClient socket = null;
            try
            {
                TcpListener tcp = (TcpListener)ar.AsyncState;
                Socket clientSocket = tcp.EndAcceptSocket(ar);
                clientConnected.Set();

                _log.LogMessage(String.Format("Listener - Connection established with {0}.", socket.Client.RemoteEndPoint));

                new Thread(
                    () => { ActionCallBack(clientSocket, _log); }
                ).Start();
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
