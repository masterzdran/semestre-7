using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Server
{
    /**
     * É quem vai gerir o timeout dos Clientes. 
     * É utilizado no momento em que se inicia uma leitura assíncrona para armazenar o IAsyncResult 
     * relativo a um pedido encaixotando-o num objecto TimeoutElement. 
     * Este último além do resultado da leitura assíncrona do pedido guarda também um valor de timeout
     * em relação à sua data de inicio (por omissão são 7 segundos). 
     * O Timeoutes tem um Timer que vai percorrendo a lista de pedidos para verificar quais deles já excederam o 
     * timeout ou quais os que já foram terminados fechando o socket ou removendo-os da lista respectivamente.
     * */
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
        
}
