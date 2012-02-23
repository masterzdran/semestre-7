using System.Collections.Generic;
using System;
using System.Net;
using Tracker;
using System.Text;
using System.IO;
using System.Net.Sockets;
namespace Tracker
{
    public sealed class Handler
    {
        internal sealed class Timeouter
        {
            class TimeoutElement
            {
                public int _endTime;
                public IAsyncResult _entry;
                public TimeoutElement() { _endTime = Environment.TickCount + TIMEOUT; }
            }

            private LinkedList<TimeoutElement> reqQueue;
            private const int TIMEOUT = 7000;
            private System.Timers.Timer worker;

            public static Timeouter Current;

            static Timeouter()
            {
                Current = new Timeouter();
            }

            private Timeouter()
            {
                reqQueue = new LinkedList<TimeoutElement>();
                worker = new System.Timers.Timer(TIMEOUT);
                worker.Elapsed += DoWork;
                worker.Enabled = true;
            }

            private void DoWork(object sender, EventArgs e)
            {
                try
                {
                    worker.Stop();
                    lock (reqQueue)
                    {

                        while (reqQueue.Count > 0)
                        {
                            if (reqQueue.First.Value._entry.IsCompleted)
                            {
                                reqQueue.RemoveFirst();
                            }
                            else if (reqQueue.First.Value._endTime < Environment.TickCount)
                            {
                                ((StateObject)reqQueue.First.Value._entry.AsyncState).Socket.Close();
                                reqQueue.RemoveFirst();
                            }
                            else
                                break;
                        }
                    }
                }
                finally { worker.Start(); }
            }

            public void AddItem(IAsyncResult entry)
            {
                lock (reqQueue)
                {
                    reqQueue.AddLast(new TimeoutElement { _entry = entry });
                }
            }
        }
        internal sealed class StateObject
        {
            public const int BufferSize = 256;

            volatile StringReader input;

            public readonly TcpClient Socket;
            public readonly byte[] Buffer;
            public readonly StringBuilder Content;
            public readonly Logger Log;

            public StateObject(TcpClient cSocket, Logger log)
            {
                Socket = cSocket;
                Log = log;
                Buffer = new byte[BufferSize];
                Content = new StringBuilder();
            }

            public NetworkStream Stream { get { return Socket.GetStream(); } }

            public string ReadLine()
            {
                lock (this)
                {
                    if (input == null) input = new StringReader(Content.ToString());

                    return input.ReadLine();
                }
            }
        }
        #region Message handlers

        static readonly Dictionary<string, Action<StateObject>> MESSAGE_HANDLERS;

        static Handler()
        {
            MESSAGE_HANDLERS = new Dictionary<string, Action<StateObject>>();
            MESSAGE_HANDLERS["REGISTER"] = ProcessRegisterMessage;
            MESSAGE_HANDLERS["UNREGISTER"] = ProcessUnregisterMessage;
            MESSAGE_HANDLERS["LIST_FILES"] = ProcessListFilesMessage;
            MESSAGE_HANDLERS["LIST_LOCATIONS"] = ProcessListLocationsMessage;
        }

        static void ProcessRegisterMessage(StateObject state)
        {
            string line;
            while (!string.IsNullOrEmpty((line = state.ReadLine())))
            {
                string[] triple = line.Split(':');
                if (triple.Length != 3)
                {
                    state.Log.LogMessage("Handler - Invalid REGISTER message.");
                    return;
                }
                IPAddress ipAddress;
                if (!IPAddress.TryParse(triple[1], out ipAddress))
                {
                    state.Log.LogMessage("Handler - Invalid REGISTER message.");
                    return;
                }
                ushort port;
                if (!ushort.TryParse(triple[2], out port))
                {
                    state.Log.LogMessage("Handler - Invalid REGISTER message.");
                    return;
                }
                Store.Instance.Register(triple[0], new IPEndPoint(ipAddress, port));
            }
            Handler.CloseConnection(state);
        }

        static void ProcessUnregisterMessage(StateObject state)
        {
            string line;
            if (!string.IsNullOrEmpty((line = state.ReadLine())))
            {
                string[] triple = line.Split(':');
                if (triple.Length != 3)
                {
                    state.Log.LogMessage("Handler - Invalid UNREGISTER message.");
                    return;
                }
                IPAddress ipAddress;
                if (!IPAddress.TryParse(triple[1], out ipAddress))
                {
                    state.Log.LogMessage("Handler - Invalid UNREGISTER message.");
                    return;
                }
                ushort port;
                if (!ushort.TryParse(triple[2], out port))
                {
                    state.Log.LogMessage("Handler - Invalid UNREGISTER message.");
                    return;
                }
                Store.Instance.Unregister(triple[0], new IPEndPoint(ipAddress, port));
            }
            else
            { state.Log.LogMessage("Handler - Invalid UNREGISTER message."); }


            Handler.CloseConnection(state);
        }

        private static void ProcessListFilesMessage(StateObject state)
        {
            string[] trackedFiles = Store.Instance.GetTrackedFiles();

            StringBuilder sb = new StringBuilder();
            foreach (string file in trackedFiles)
                sb.AppendLine(file);

            byte[] response = Encoding.ASCII.GetBytes(sb.ToString());
            state.Stream.BeginWrite(response, 0, response.Length,
                ProcessListFilesEnd, state);
        }
        private static void ProcessListFilesEnd(IAsyncResult iaR)
        {
            StateObject state = (StateObject)iaR.AsyncState;
            state.Stream.EndWrite(iaR);
            Handler.CloseConnection(state);
        }

        private static void ProcessListLocationsMessage(StateObject state)
        {
            string line;
            if (!string.IsNullOrEmpty((line = state.ReadLine())))
            {
                if (Store.Instance.ContainsKey(line))
                {
                    IPEndPoint[] fileLocations = Store.Instance.GetFileLocations(line);

                    StringBuilder sb = new StringBuilder();
                    foreach (IPEndPoint endpoint in fileLocations)
                        sb.AppendLine(string.Format("{0}:{1}", endpoint.Address, endpoint.Port));

                    byte[] response = Encoding.ASCII.GetBytes(sb.ToString());
                    state.Stream.BeginWrite(response, 0, response.Length
                        , ProcessListLocationsEnd, state);
                }
                else
                { state.Log.LogMessage("Handler - File indicated in LIST_LOCATIONS message not being hosted."); }
            }
            else
            { state.Log.LogMessage("Handler - Invalid LIST_LOCATIONS message."); }
        }
        private static void ProcessListLocationsEnd(IAsyncResult iaR)
        {
            StateObject state = (StateObject)iaR.AsyncState;
            state.Stream.EndWrite(iaR);
            Handler.CloseConnection(state);
        }

        #endregion
        static void ReadDataCallback(IAsyncResult ar)
        {
            StateObject state = (StateObject)ar.AsyncState;
            try
            {
                int bytesRead = state.Stream.EndRead(ar);
                state.Log.LogMessage("Waiting.....");
                if (bytesRead > 0)
                {
                    state.Content.Append(Encoding.ASCII.GetString(state.Buffer, 0, bytesRead));

                    // Tests if one blank line was sent meaning client submited request
                    if (state.Content.ToString().Contains("\r\n\r\n"))
                    {
                        string requestType = state.ReadLine();
                        // If request is not empty and client typed a valid command
                        if (!String.IsNullOrEmpty(requestType) && MESSAGE_HANDLERS.ContainsKey(requestType.ToUpper()))
                            MESSAGE_HANDLERS[requestType](state);
                        else
                        {
                            state.Log.LogMessage("Handler - Unknown message type.");
                            Handler.CloseConnection(state);
                        }
                    }
                    // If no blank line was sent continues to read the stream
                    else
                    {
                        state.Stream.BeginRead(state.Buffer, 0, StateObject.BufferSize,
                           new AsyncCallback(ReadDataCallback), state);
                    }
                }
                // If no bytes were received the connection was lost
                else
                {
                    state.Log.LogMessage("Handler - Connection to client was lost.");
                    state.Socket.Close();
                }
            }
            catch (IOException) { state.Log.LogMessage("Handler - Connection closed by client.\n"); }
            catch (ObjectDisposedException) { state.Log.LogMessage("Handler - Timeout expired while receivig request. Servicing ending.\n"); }
            catch (InvalidOperationException) { state.Log.LogMessage("Handler - Timeout expired while receivig request. Servicing ending.\n"); }
        }

        private static void CloseConnection(StateObject state)
        {
            state.Log.LogMessage(string.Format("Handler - Closing connection @ {0}", state.Socket.Client.RemoteEndPoint));
            if (state.Socket.Connected) state.Socket.Close();
            Program.ShowInfo(Store.Instance);
        }

        /// <summary>
        /// Performs request servicing.
        /// </summary>
        public static void StartAcceptTcpClient(TcpClient socket, Logger log)
        {
            try
            {
                StateObject state = new StateObject(socket, log);

                IAsyncResult iaR = state.Stream.BeginRead(state.Buffer, 0, StateObject.BufferSize,
                    new AsyncCallback(ReadDataCallback), state);

                Timeouter.Current.AddItem(iaR);
            }
            catch (IOException ioe) { log.LogMessage(String.Format("Handler - Connection closed by client.\n{0}", ioe)); }
            catch (SocketException se) { log.LogMessage(String.Format("Handler - Client request timed out. Servicing ending.\n{0}", se)); }
        }
    }

}