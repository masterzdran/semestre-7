using System;

using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Parte1
{
    class Q1
    {
        public static ParallelLoopResult For(int start, int end, ParallelOptions options, Action<int> body)
        {
            ParallelLoopResult res = Parallel.For(start, end, body);
            int x = options.MaxDegreeOfParallelism;
            CancellationToken ct = options.CancellationToken;

            for (int idx = start; idx <= end; ++idx)
            {
                
            }
            
            





            return res;
        } 


    }
}
