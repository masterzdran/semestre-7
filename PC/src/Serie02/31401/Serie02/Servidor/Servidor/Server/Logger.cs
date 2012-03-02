using System;
using System.IO;
using System.Threading;
using System.Collections.Generic;

namespace Tracker
{
	public class Logger
	{
		private readonly TextWriter writer;
		private DateTime start_time;
		private int num_requests;
        private readonly LinkedList<String> _queue;

		public Logger() : this(Console.Out) {}
		public Logger(string logfile) : this(new StreamWriter(new FileStream(logfile, FileMode.Append, FileAccess.Write))) {}
		public Logger(TextWriter awriter)
		{
            
		    num_requests = 0;
		    writer = awriter;
            _queue = new LinkedList<string>();
		}

	    public void Start()
		{
            lock (writer)
            {
                new Thread(
                    () =>
                        {
                            Thread.CurrentThread.IsBackground = true;
                            Thread.CurrentThread.Priority = ThreadPriority.Lowest;
                            LoggerWorker();
                        }
                ).Start();
                start_time = DateTime.Now;
                LogMessage(String.Format("::- LOG STARTED @ {0} -::", DateTime.Now));
            }
		}

		public void LogMessage(string msg)
		{
            lock (_queue)
            {
                 _queue.AddLast(String.Format("{0}: {1}", DateTime.Now, msg));
                Monitor.PulseAll(_queue);
            }
		}

		public void IncrementRequests()
		{
            Interlocked.Increment(ref num_requests);
		}

		public void Stop()
		{
            lock (_queue)
            {
                long elapsed = DateTime.Now.Ticks - start_time.Ticks;
                LogMessage(String.Format("Running for {0} second(s)", elapsed / 10000000L));
                LogMessage(String.Format("Number of request(s): {0}", num_requests));
                LogMessage(String.Format("::- LOG STOPPED @ {0} -::", DateTime.Now));
                writer.Close();
            }
		}
        public void LoggerWorker()
        {
            lock (_queue) {
                while (true)
                {
                    if (_queue.Count == 0)
                        Monitor.Wait(_queue);
                    LinkedListNode<String> log = _queue.First;
                    _queue.RemoveFirst();
                    writer.WriteLine(log);
                    Monitor.PulseAll(_queue);
                }
            
            }
        }
	}
}
