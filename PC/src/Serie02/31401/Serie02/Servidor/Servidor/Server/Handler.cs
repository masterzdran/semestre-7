using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net;

namespace Tracker
{
    public sealed  class Handler
    {
        #region Message handlers
        private static readonly Dictionary<string, Action<StreamReader, StreamWriter, Logger>> MESSAGE_HANDLERS;
        static Handler()
        {
            MESSAGE_HANDLERS = new Dictionary<string, Action<StreamReader, StreamWriter, Logger>>();
            MESSAGE_HANDLERS["REGISTER"] = ProcessRegisterMessage;
            MESSAGE_HANDLERS["UNREGISTER"] = ProcessUnregisterMessage;
            MESSAGE_HANDLERS["LIST_FILES"] = ProcessListFilesMessage;
            MESSAGE_HANDLERS["LIST_LOCATIONS"] = ProcessListLocationsMessage;
        }
        private static void ProcessRegisterMessage(StreamReader input, StreamWriter output, Logger log)
        {
            lock (MESSAGE_HANDLERS)
            {
                string line;
                while ((line = input.ReadLine()) != null && line != string.Empty)
                {
                    string[] triple = line.Split(':');
                    if (triple.Length != 3)
                    {
                        log.LogMessage("Handler - Invalid REGISTER message.");
                        return;
                    }
                    IPAddress ipAddress = IPAddress.Parse(triple[1]);
                    ushort port;
                    if (!ushort.TryParse(triple[2], out port))
                    {
                        log.LogMessage("Handler - Invalid REGISTER message.");
                        return;
                    }
                    Store.Instance.Register(triple[0], new IPEndPoint(ipAddress, port));
                }
            }
        }
        private static void ProcessUnregisterMessage(StreamReader input, StreamWriter output, Logger log)
        {
            lock (MESSAGE_HANDLERS)
            {
                string line;
                while ((line = input.ReadLine()) != null && line != string.Empty)
                {
                    string[] triple = line.Split(':');
                    if (triple.Length != 3)
                    {
                        log.LogMessage("Handler - Invalid UNREGISTER message.");
                        return;
                    }
                    IPAddress ipAddress = IPAddress.Parse(triple[1]);
                    ushort port;
                    if (!ushort.TryParse(triple[2], out port))
                    {
                        log.LogMessage("Handler - Invalid UNREGISTER message.");
                        return;
                    }
                    Store.Instance.Unregister(triple[0], new IPEndPoint(ipAddress, port));
                }
            }
        }
        private static void ProcessListFilesMessage(StreamReader input, StreamWriter output, Logger log)
        {
            input.ReadLine();

            string[] trackedFiles = Store.Instance.GetTrackedFiles();
            foreach (string file in trackedFiles)
                output.WriteLine(file);
            output.WriteLine();
            output.Flush();
        }
        private static void ProcessListLocationsMessage(StreamReader input, StreamWriter output, Logger log)
        {
            string line = input.ReadLine();
            input.ReadLine();

            IPEndPoint[] fileLocations = Store.Instance.GetFileLocations(line);
            foreach (IPEndPoint endpoint in fileLocations)
                output.WriteLine(string.Format("{0}:{1}", endpoint.Address, endpoint.Port));
            output.WriteLine();
            output.Flush();
        }
        #endregion


        private readonly StreamReader input;
        private readonly StreamWriter output;
        private readonly Logger log;
        public Handler(Stream connection, Logger log)
        {
            this.log = log;
            output = new StreamWriter(connection);
            input = new StreamReader(connection);
        }
        public void Run()
        {
            try
            {
                string requestType;
                while ((requestType = input.ReadLine()) != null && requestType != string.Empty)
                {
                    requestType = requestType.ToUpper();
                    if (!MESSAGE_HANDLERS.ContainsKey(requestType))
                    {
                        log.LogMessage("Handler - Unknown message type. Servicing ending.");
                        return;
                    }
                    MESSAGE_HANDLERS[requestType](input, output, log);
                }
            }
            catch (IOException ioe)
            {
                log.LogMessage(String.Format("Handler - Connection closed by client {0}", ioe));
            }
            finally
            {
                input.Close();
                output.Close();
            }
        }
    }

}
