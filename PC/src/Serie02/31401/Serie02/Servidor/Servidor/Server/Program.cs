using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;

namespace Tracker
{
    

    class Program
    {
        public static void ShowInfo(Store store)
        {
            foreach (string fileName in store.GetTrackedFiles())
            {
                Console.WriteLine(fileName);
                foreach (IPEndPoint endPoint in store.GetFileLocations(fileName))
                {
                    Console.Write(endPoint + " ; ");
                }
                Console.WriteLine();
            }
            Console.WriteLine();
        }

/*
        static void TestStore()
        {
            Store store = Store.Instance;

            store.Register("xpto", new IPEndPoint(IPAddress.Parse("193.1.2.3"), 1111));
            store.Register("xpto", new IPEndPoint(IPAddress.Parse("194.1.2.3"), 1111));
            store.Register("xpto", new IPEndPoint(IPAddress.Parse("195.1.2.3"), 1111));
            ShowInfo(store);
            Console.ReadLine();
            store.Register("ypto", new IPEndPoint(IPAddress.Parse("193.1.2.3"), 1111));
            store.Register("ypto", new IPEndPoint(IPAddress.Parse("194.1.2.3"), 1111));
            ShowInfo(store);
            Console.ReadLine();
            store.Unregister("xpto", new IPEndPoint(IPAddress.Parse("195.1.2.3"), 1111));
            ShowInfo(store);
            Console.ReadLine();

            store.Unregister("xpto", new IPEndPoint(IPAddress.Parse("193.1.2.3"), 1111));
            store.Unregister("xpto", new IPEndPoint(IPAddress.Parse("194.1.2.3"), 1111));
            ShowInfo(store);
            Console.ReadLine();
        }
*/
        
        public static void Main(string[] args)
        {
            // Checking command line arguments
            ushort port=0;
            if (args.Length != 1 || !ushort.TryParse(args[0], out port))
            {
                Console.WriteLine("Utilização: {0} <numeroPortoTCP>", AppDomain.CurrentDomain.FriendlyName);
                Environment.Exit(1);
            }

            // Start servicing
            Logger log = new Logger();
            log.Start();
            try
            {
                Listener l =  new Listener(port);
                l.Run();
                Console.ReadLine();
            }
            finally
            {
                log.Stop();
            }
        }
    }
}
