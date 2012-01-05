using System;
using System.Threading;

private class MAsyncResult : IAsyncResult
{
    public readonly String a1;
    public readonly int a2;
    public int a3;
    public int a4;
    public readonly AsyncCallback callback;
    public int result;
    
    private readonly ManualResetEvent _event;
    private bool done;

    public bool IsCompleted { get{ return done; } }
    public WaitHandle AsyncWaitHandle { get{ return _event; } }
    
    public MAsyncResult(String a1, int a2, int a3, AsyncCallback call)
    {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.callback = call;
        _event = new ManualResetEvent(false);
    }

    public void Publish(int result)
    {
        this.result = result;
        _event.Set();
    }
}

public class Aula20120104
{

    public int M(String a1, int a2, ref int a3, out int a4)
    {
        return a2 + a3 + a4;
    }

    public IAsyncResult BeginM(String a1, int a2, int a3, AsyncCallback call)
    {

        MAsyncResult result = new MAsyncResult(a1, a2, a3, call);

        ThreadPool.QueueUserWorkItem((state) =>
        {
            MAsyncResult res = state as MAsyncResult;
            res.Publish(M(res.a1, res.a2, ref res.a3, out res.a4));
            res.callback(res);
        }, result);

        return result;

    }

    public int EndM(out int a3, out int a4, IAsyncResult token)
    {
        MAsyncResult res = token as MAsyncResult;
        if(res == null) throw new IllegalArgumentException();
        if (!res.IsCompleted) res.AsyncWaitHandle.WaitOne();
        a3 = res.a3;
        a4 = res.a4;
        return res.result;
    }

    //delegate void AsyncCallback(IAsyncResult iar)
    //delegate void WaitCallback(Object state)

    
    void Main()
    {
        int x = 5, y;
        //int res = M("SLB", 4, ref x, out y);

        /*
        IAsyncResult ar1 = BeginM("SLB", 4, x);
        IAsyncResult ar2 = BeginM("SCP", 3, x);
        int res = EndM(out x, out y, ar1);
        */

        IAsyncResult res = BeginM("SLB", 4, 1, (asyncRes) => {
            int xout, yout;
            int result = EndM(xout, yout, asyncRes);
            Console.WriteLine();
        });
        

    }

}