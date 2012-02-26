using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Collections.Concurrent;

namespace Parte1
{
    public class Pipeline<TInput, TOutput>
    {

        LinkedList<PipelineFilter<TInput, TOutput>> filters = null;

        public Pipeline(Func<TInput, TOutput> stage)
        {
            filters = new LinkedList<PipelineFilter<TInput, TOutput>>();
            filters.AddFirst(new PipelineFilter<TInput, TOutput>(new BlockingCollection<TInput>(), stage));
        }

        /* Retorna um novo Pipeline ao qual foi acrescentado um passo que converte  
           elementos do tipo TOutput em elementos do tipo TNextOutput */ 
        public Pipeline<TInput, TNextOutput> Next<TNextOutput>(Func<TOutput, TNextOutput> nextStage)
        {
            //if Pipeline wasn't properly initialized return null
            if (filters == null) return null;
            
            //Pipeline<TOutput, TNextOutput> 
            var newPipeline = new Pipeline<TOutput, TNextOutput>(nextStage);
            
            BlockingCollection<TOutput> bc = filters.Last.Value.myOutput;
            PipelineFilter<TOutput, TNextOutput> filter = new PipelineFilter<TOutput, TNextOutput>(filters.Last.Value.myOutput, nextStage);
            filters.AddLast(filter);
            return this;
        }


        public IEnumerable<TOutput> Run(IEnumerable<TInput> _source, CancellationToken token)
        {
            try
            {
                foreach (PipelineFilter<TInput, TOutput> filter in filters)
                {

                }


            }
            catch (Exception e)
            { 
            
            }
            return filters.Last.Value.myOutput;        
        }

        public IEnumerable<TOutput> Run(IEnumerable<TInput> _source)
        {
            return this.Run(_source, new CancellationToken());
        }
 
        public   static void Test() { 
            // sequência de inteiros de 1 a 10  
            var source = Enumerable.Range(1, 10).ToArray();
            
            var p1 = new Pipeline<int, int>(i => i * 3)
                            .Next(i => i /2);
            var p2 = p1.Next(i => (i % 2 == 0) ? "Par" : "Impar");
            //var p3 = p2.Next(i => i.Length);
            foreach (string i in p3.Run(source)) 
                Console.WriteLine(i);
        }

        class PipelineFilter<TInput, TOutput>
        {
            public BlockingCollection<TInput> myInput;
            public BlockingCollection<TOutput> myOutput;
            Func<TInput, TOutput> myFunc = null;

            public PipelineFilter(BlockingCollection<TInput> input, Func<TInput, TOutput> func)
            {
                myInput = input;
                myOutput = new BlockingCollection<TOutput>();
                myFunc = func;
            }

            public void Run(CancellationToken token) 
            {

                while (!myInput.IsCompleted)
                {
                    TInput inputItem;
                    myInput.TryTake(out inputItem, int.MaxValue, token);

                    if (inputItem != null)
                    {
                        TOutput outputItem = myFunc(inputItem);
                        myOutput.TryAdd(myFunc(inputItem), int.MaxValue, token);
                    }
                }
                myOutput.CompleteAdding();
            }
        }
    }

    
}
