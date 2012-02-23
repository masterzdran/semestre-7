using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PC1112SI_Classes
{
    class PC1112SI_Serie2
    {
        /*
            public static ParallelLoopResult For(
	            int fromInclusive,
	            int toExclusive,
	            ParallelOptions parallelOptions,
	            Action<int> body
            )
         */

        public static int GetBestWorkerNumber()
        {
            // Current -> Gets the TaskScheduler associated with the currently executing task.
            return GetBestWorkerNumber(TaskScheduler.Current);
        }

        public static int GetBestWorkerNumber(TaskScheduler scheduler)
        {
            // TaskScheduler -> Represents an object that handles the low-level work of queuing tasks onto threads.
            // MaximumConcurrencyLevel -> Indicates the maximum concurrency level this
            //                          System.Threading.Tasks.TaskScheduler is able to support.
            return scheduler.MaximumConcurrencyLevel;
        }

        // Executes a for loop in which iterations may run in parallel and loop options can be configured.
        public static ParallelLoopResult For2(int start, int end, ParallelOptions options, Action<int> body)
        {
            // Number of tasks to be launched
            int num = Math.Min(GetBestWorkerNumber(),
                // MaxDegreeOfParallelism-> Gets the maximum degree of parallelism enabled by this ParallelOptions instance.
                (options != null) ? options.MaxDegreeOfParallelism : int.MaxValue);
            
            //todo : options.TaskScheduler
            
            try
            {
                //if (options != null) options.CancellationToken.ThrowIfCancellationRequested();

                Task.Factory.StartNew(() =>
                {
                    object _lock = new object();
                    Task[] tasks = new Task[num];
                    for (int idx = start; idx < end; ++idx)
                    {
                        int index = idx;
                        options.CancellationToken.ThrowIfCancellationRequested();
                        Task.Factory.StartNew(() =>
                        {
                            
                            lock (_lock)
                            {
                                
                            }
                        }, TaskCreationOptions.AttachedToParent);
                    }
                }).Wait();



            }
            catch (AggregateException aggEx)
            {
                foreach (Exception ex in aggEx.InnerExceptions)
                {
                    Console.WriteLine(string.Format("Caught exception '{0}'",
                        ex.Message));
                }
            }
            catch (OperationCanceledException opcEx)
            {
                Console.WriteLine(string.Format("Caught exception '{0}'",
                        opcEx.Message));
            }


        }


        // Executes a for loop in which iterations may run in parallel and loop options can be configured.
        public static ParallelLoopResult For(int start, int end, ParallelOptions options, Action<int> body)
        {

            ParallelLoopResult result = new ParallelLoopResult();

            try
            {
                //if (options != null) options.CancellationToken.ThrowIfCancellationRequested();
                options.CancellationToken.ThrowIfCancellationRequested();
                Task.Factory.StartNew(() =>
                {
                    object _lock = new object();
                    for (int idx = start; idx < end; ++idx)
                    {
                        int index = idx;
                        Task.Factory.StartNew(() =>
                        {
                            lock (_lock)
                            {
                                body.Invoke(index);
                            }
                        }, TaskCreationOptions.AttachedToParent);
                    }
                }).Wait();

            }
            catch (AggregateException aggEx)
            {
                foreach (Exception ex in aggEx.InnerExceptions)
                {
                    Console.WriteLine(string.Format("Caught exception '{0}'",
                        ex.Message));
                }
            }
            catch (OperationCanceledException opcEx)
            {
                Console.WriteLine(string.Format("Caught exception '{0}'",
                        opcEx.Message));
            }

            return result;
        }

    }
}
