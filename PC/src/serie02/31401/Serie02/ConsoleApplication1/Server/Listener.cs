using System.Net.Sockets;
using System.Net;
using System;
namespace Server
{
    public sealed class Listener
    {
        private readonly int portNumber;
        public Listener(int _portNumber)
        {
            portNumber = _portNumber;
        }
        public void Run(Logger log)
        {
            TcpListener srv = null;
            try
            {
                srv = new TcpListener(IPAddress.Loopback, portNumber);
                srv.Start();
                while (true)
                {
                    log.LogMessage("Listener - Waiting for connection requests.");
                    TcpClient socket = srv.AcceptTcpClient();
                    socket.LingerState = new LingerOption(true, 10);
                    log.LogMessage(String.Format("Listener - Connection established with {0}.", socket.Client.RemoteEndPoint));
                    Handler.StartAcceptTcpClient(socket, log);
                }
            }
            finally
            {
                log.LogMessage("Listener - Ending.");
                srv.Stop();
            }
        }
    }
}