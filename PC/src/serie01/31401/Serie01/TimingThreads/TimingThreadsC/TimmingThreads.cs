using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Diagnostics;



namespace TimingThreadsC
{
    class TimmingThreads
    {
        readonly static int _COUNT = 1000000;
        
        static void Switch(){
            int c = _COUNT;
	        do{Thread.Yield();}while(--c != 0);
        }

        static void SwitchingWindowsThreads(){

            Stopwatch watch;
            long startCycleClock, endCycleClock;

            //Setting processor afinity
            Process.GetCurrentProcess().ProcessorAffinity = new IntPtr(1);
            Thread.BeginThreadAffinity();
            

	        Console.WriteLine("-----------------------------------------\n");
            Console.WriteLine("Switching Windows Threads\n");
            Thread t1, t2;
            startCycleClock = DateTime.UtcNow.Ticks;
            watch = new Stopwatch();

            t1 = new Thread(delegate() { Switch(); });
            t2 = new Thread(delegate() { Switch(); });
            //setting Threads priority
            t1.Priority = ThreadPriority.Highest;
            t2.Priority = ThreadPriority.Highest;

            watch.Start();
            t1.Start();
            t2.Start();
            t1.Join(); t2.Join();

            
            
            watch.Stop();
            //get post processing times
            endCycleClock = DateTime.UtcNow.Ticks;
            TimeSpan ts = watch.Elapsed;
            long frequency = Stopwatch.Frequency;
            
            //remove afinity
            Thread.EndThreadAffinity();
            long total = (endCycleClock - startCycleClock) * 100; //A single tick represents one hundred nanoseconds or one ten-millionth of a second. There are 10,000 ticks in a millisecond.
            //TODO: VERIFY values
            Console.WriteLine("Absolute Time is {0} ns {1}\n", ts.Milliseconds, (1000L*1000L*1000L) / frequency);
            Console.WriteLine("Number of ns {0}\n", (total)/(_COUNT*2));
}

        static void GetInfoComputer() 
        {
            Console.WriteLine(
                "Numero de Processadores : {0} \nArquitectura {1}", 
                Environment.ProcessorCount, 
                (Environment.Is64BitOperatingSystem) ? "64 Bits" : "32 Bits"
            );
        }


        
        public static int Main(string[] args)
        {
            GetInfoComputer();
            SwitchingWindowsThreads();
            Console.ReadLine();
            return 0;
        }
    }
}
