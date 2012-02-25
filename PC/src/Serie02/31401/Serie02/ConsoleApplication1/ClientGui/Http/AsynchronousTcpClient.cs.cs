using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.IO;

namespace ClientGui.Http
{
    internal class AsynchronousTcpClient
    {
        const int BUFFERSIZE = 256;

        public EventHandler EndRequest    = null;

        volatile Socket        socket = null;
        volatile byte[]        buffer     = null;

        readonly StringReader  reader = null;
        readonly IPEndPoint    remoteEndPoint   = null;
        readonly StringBuilder output     = null;


        public AsynchronousTcpClient(IPEndPoint remoteEp, StringReader dataReader )
        {
            if (remoteEp   == null) throw new ArgumentNullException("remoteEp");
            if (dataReader == null) throw new ArgumentNullException("dataReader");

            this.remoteEndPoint   = remoteEp;
            this.reader = dataReader;
            this.output     = new StringBuilder();
        }

        public AsynchronousTcpClient(string hostName, int port, StringReader dataReader) : this(new IPEndPoint(Dns.GetHostEntry(hostName).AddressList[0], port), dataReader) { }


        public void Start()
        {
            try
            {
                socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                socket.BeginConnect(remoteEndPoint, new AsyncCallback(ConnectCallback), this);
            }
            catch (Exception e)
            {
                if (EndRequest != null) EndRequest.Invoke(this, new EndRequestArgs(e.ToString(), RequestState.Error));
            }
        }

        public void Stop()
        {
            if (socket.Connected)
            {
                socket.Shutdown(SocketShutdown.Both);
                socket.Close();
            }
        }

        private void ConnectCallback(IAsyncResult ar)
        {
            try
            {
                socket.EndConnect(ar);
                Send(reader.ReadToEnd());
            }
            catch (Exception e)
            {
                if (EndRequest != null) EndRequest.Invoke(this, new EndRequestArgs(e.ToString(), RequestState.Error));
            }
        }

        private void Receive()
        {
            try
            {
                buffer = new byte[BUFFERSIZE];

                socket.BeginReceive(buffer, 0, BUFFERSIZE, 0,
                    new AsyncCallback(ReceiveCallback), this);
            }
            catch (Exception e)
            {
                if (EndRequest != null) EndRequest.Invoke(this, new EndRequestArgs(e.ToString(), RequestState.Error));
            }
        }

        private void ReceiveCallback(IAsyncResult ar)
        {
            try
            {
                int bytesRead = socket.EndReceive(ar);

                if (bytesRead > 0)
                {
                    output.Append(Encoding.ASCII.GetString(buffer, 0, bytesRead));

                    socket.BeginReceive(buffer, 0, BUFFERSIZE, 0,
                        new AsyncCallback(ReceiveCallback), this);
                }
                else
                {
                    if (EndRequest != null) EndRequest.Invoke(this, new EndRequestArgs(output.ToString(), RequestState.Success));
                }
            }
            catch (Exception e)
            {
                if (EndRequest != null) EndRequest.Invoke(this, new EndRequestArgs(e.ToString(), RequestState.Error));
            }
        }

        private void Send(String data)
        {
            byte[] byteData = Encoding.ASCII.GetBytes(data);

            socket.BeginSend(byteData, 0, byteData.Length, 0,
                new AsyncCallback(SendCallback), this);
        }

        private void SendCallback(IAsyncResult ar)
        {
            try
            {
                int bytesSent = socket.EndSend(ar);

                Receive();
            }
            catch (Exception e)
            {
                if (EndRequest != null) EndRequest.Invoke(this, new EndRequestArgs(e.ToString(), RequestState.Error));
            }
        }
    }
}
