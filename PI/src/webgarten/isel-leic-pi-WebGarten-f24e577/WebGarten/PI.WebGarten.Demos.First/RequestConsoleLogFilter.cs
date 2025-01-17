﻿namespace PI.WebGarten.Demos.First
{
    using System;
    using System.Net;

    using PI.WebGarten.Pipeline;

    public class RequestConsoleLogFilter : IHttpFilter
    {
        private readonly string _filterName;

        private IHttpFilter _nextFilter;

        public RequestConsoleLogFilter(string filterName)
        {
            this._filterName = filterName;
        }

        public string Name
        {
            get {
                return _filterName;
            }
        }

        public void SetNextFilter(IHttpFilter nextFilter)
        {
            _nextFilter = nextFilter;
        }

        public HttpResponse Process(HttpListenerContext ctx)
        {
            Console.WriteLine("[LogFilter]: Request for URI '{0}'", ctx.Request.Url);
            return _nextFilter.Process(ctx);
        }
    }
}