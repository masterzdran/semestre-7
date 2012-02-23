using System;
using System.Threading;

namespace RendezvousChannel
{
    public static class SyncUtils
    {
        private static void EnterUninterruptable(object Lock, out Boolean interrupted)
        {
            interrupted = false;
            while (true)
            {
                try
                {
                    Monitor.Enter(Lock);
                    break;
                }
                catch (ThreadInterruptedException)
                {

                    interrupted = true;
                }
            }
        }
        public static void Wait(object Lock, object Condition, Int32 timeout)
        {
            if (Lock == Condition)
            {
                Monitor.Wait(Lock, timeout);
                return;
            }
            Monitor.Enter(Condition);
            Monitor.Exit(Lock);

            try
            {
                Monitor.Wait(Condition, timeout);
            }
            finally
            {
                Monitor.Exit(Condition);
                Boolean interrupted;
                EnterUninterruptable(Lock, out interrupted);
                if (interrupted) throw new ThreadInterruptedException();
            }
        }
        public static void Notify(Object Lock, Object Condition)
        {
            if (Lock == Condition)
            {
                Monitor.Pulse(Lock);
                return;
            }
            Boolean interrupted;
            EnterUninterruptable(Lock, out interrupted);
            Monitor.Pulse(Condition);
            Monitor.Exit(Condition);
            if (interrupted) Thread.CurrentThread.Interrupt();
        }
        public static void NotifyAll(Object Lock, Object Condition)
        {
            if (Lock == Condition)
            {
                Monitor.PulseAll(Lock);
                return;
            }
            Boolean interrupted;
            EnterUninterruptable(Lock, out interrupted);
            Monitor.PulseAll(Condition);
            Monitor.Exit(Condition);
            if (interrupted) Thread.CurrentThread.Interrupt();
        }

        public static Int32 AdjustTimeout(ref Int32 lastTime, ref Int32 timeout)
        {
            if (timeout != Timeout.Infinite)
            {
                Int32 now = Environment.TickCount;
                Int32 elapsed = (now == lastTime) ? 1 : now - lastTime;
                if (elapsed >= timeout)
                {
                    timeout = 0;
                }
                else
                {
                    timeout -= elapsed;
                    lastTime = now;
                }
            }

            return timeout;
        }
    }
}
