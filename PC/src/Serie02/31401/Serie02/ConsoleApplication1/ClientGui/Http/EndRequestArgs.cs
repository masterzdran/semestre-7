using System;
using System.IO;

namespace ClientGui.Http
{
    internal class EndRequestArgs : EventArgs
    {
        readonly string       content = null;
        readonly RequestState state   = RequestState.Unknown;


        public EndRequestArgs(string contect, RequestState state)
        {
            this.content = contect;
            this.state   = state;
        }

        public string       Content { get { return content; } }
        public RequestState State   { get { return state; } }

    }
}
