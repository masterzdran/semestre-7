using System;
using System.IO;
using System.Threading;

namespace Tracker
{
	public class Logger
	{
		private readonly TextWriter writer;
		private DateTime start_time;
		private int num_requests;

		public Logger() : this(Console.Out) {}
		public Logger(string logfile) : this(new StreamWriter(new FileStream(logfile, FileMode.Append, FileAccess.Write))) {}
		public Logger(TextWriter awriter)
		{
		    num_requests = 0;
		    writer = awriter;
		}

	    public void Start()
		{
            lock (writer)
            {
                start_time = DateTime.Now;
                writer.WriteLine();
                writer.WriteLine(String.Format("::- LOG STARTED @ {0} -::", DateTime.Now));
                writer.WriteLine();
            }
		}

		public void LogMessage(string msg)
		{
            lock (writer)
            {
                writer.WriteLine(String.Format("{0}: {1}", DateTime.Now, msg));
            }
		}

		public void IncrementRequests()
		{
            Interlocked.Increment(ref num_requests);
		}

		public void Stop()
		{
            lock (writer)
            {
                long elapsed = DateTime.Now.Ticks - start_time.Ticks;
                writer.WriteLine();
                LogMessage(String.Format("Running for {0} second(s)", elapsed / 10000000L));
                LogMessage(String.Format("Number of request(s): {0}", num_requests));
                writer.WriteLine();
                writer.WriteLine(String.Format("::- LOG STOPPED @ {0} -::", DateTime.Now));
                writer.Close();
            }
		}
	}
}
