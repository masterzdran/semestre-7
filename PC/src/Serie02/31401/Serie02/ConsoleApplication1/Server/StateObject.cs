using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net.Sockets;

namespace Server
{
    /**
     * Representa o pedido de um Cliente. 
     * Numa fase inicial apenas tem informação relativao ao socket onde ele está pendurado 
     * e o logger a utilizar para "dumpar" mensagens.
     * Por cada Cliente ligado é criada uma instância de StateObject 
     * que essencialmente contém o buffer para onde vão ser lidos os dados e a stream de 
     * onde eles vêm iniciando-se de imediato a leitura assíncrona 
     * dos dados usando o BeginRead da stream. 
     * Como resultado dessa leitura é criado um IAsyncResult que é guardado no Timeouter.
     * */
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
}
