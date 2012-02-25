//[Nuno Cancelo] : Comentários apagados, porque tornavam dificil a leitura do código.
using System;
using System.IO;
using System.Threading;
using System.Collections.Generic;

namespace Server
{
    enum LoggerStatus { Started, Stoped }
    public class Logger
    {
        const int SIZE = 2048;
        readonly TextWriter writer;
        DateTime start_time;
        int num_requests;
        volatile LoggerStatus status;
        volatile Thread thWorker;
        readonly LinkedList<string> queue;
        public Logger() : this(Console.Out) { }
        public Logger(string logfile) : this(new StreamWriter(new FileStream(logfile, FileMode.Append, FileAccess.Write))) { }
        public Logger(TextWriter awriter)
        {
            num_requests = 0;
            writer = awriter;
            status = LoggerStatus.Stoped;
            queue = new LinkedList<string>();
        }

        void WriterWork()
        {
            while (true)
            {
                string queueEntry = null;
                lock (queue)
                {
                    if (queue.Count > 0)
                    {
                        queueEntry = queue.First.Value;
                        queue.RemoveFirst();
                    }
                    else
                    {
                        try
                        {
                            Monitor.Wait(queue);
                        }
                        catch (ThreadInterruptedException){}
                    }
                }
                if (queueEntry != null) writer.Write(queueEntry);
            }
        }

        void Enqueue(string text)
        {
            lock (queue)
            {
                if (queue.Count < SIZE)
                {
                    queue.AddLast(text);
                    Monitor.PulseAll(queue);
                    return;
                }

                while (true)
                {
                    Monitor.Wait(queue);
                    if (queue.Count < SIZE)
                    {
                        queue.AddLast(text);
                        Monitor.PulseAll(queue);
                        return;
                    }
                }
            }
        }

        void WriteLine(string text)
        {
            Enqueue(string.Format("{0}{1}", text, Environment.NewLine));
        }

        void WriteLine()
        {
            WriteLine("");
        }

        public bool Start()
        {
            if (status == LoggerStatus.Started) return false;
            lock (this)
            {
                if (status == LoggerStatus.Started) return false;

                start_time = DateTime.Now;
                status = LoggerStatus.Started;
            }

            thWorker = new Thread(new ThreadStart(WriterWork));
            thWorker.Priority = ThreadPriority.Lowest;
            thWorker.Start();

            WriteLine();
            WriteLine(String.Format("::- LOG STARTED @ {0} -::", start_time));
            WriteLine();

            return true;
        }


        public void LogMessage(string msg)
        {
            WriteLine(String.Format("{0}: {1}", DateTime.Now, msg));
        }

        public int IncrementRequests()
        {
            return System.Threading.Interlocked.Increment(ref num_requests);
        }

        public bool Stop()
        {
            WriteLine();
            LogMessage(String.Format("Running for {0} second(s)", (DateTime.Now.Ticks - start_time.Ticks) / 10000000L));
            LogMessage(String.Format("Number of request(s): {0}", num_requests));
            WriteLine();
            LogMessage(String.Format("::- LOG STOPPED @ {0} -::", DateTime.Now));
            lock (this)
            {
                if (status == LoggerStatus.Stoped) return false;
                thWorker.Interrupt();
                status = LoggerStatus.Stoped;
            }

            return true;
        }
    }
}
