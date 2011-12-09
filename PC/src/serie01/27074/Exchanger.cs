/*
 * Implemente em C# o sincronizador exchanger Exchanger<T> que permite a troca,
 * entre pares de threads, de mensagens definidas por instâncias do tipo T. A
 * classe que implementa o sincronizador deve definir, pelo menos, o seguinte método:
 *      Método bool Exchange(T mine, int timeout, out T yours)
 * que é chamado pelas threads para oferecer uma mensagem (parâmetro mine) e receber
 * a mensagem oferecida pela thread com que emparelham (parâmetro yours). Quando a
 * troca de mensagens não pode ser realizada de imediato (não existe já uma thread
 * bloqueada), a thread corrente fica bloqueada até que outra thread invoque o método
 * Exchange, seja interrompida ou expire o limite de tempo, especificado através do
 * parâmetro timeout.
 */

using System;
using System.Threading;

namespace PC1112SI_1aSerie
{
    public sealed class Exchanger<T> {

        private readonly ThreadElem _threadElem;
        
        class ThreadElem
        {
            public T _message;
        }

        public Exchanger()
        {
            _threadElem = new ThreadElem();
        }

        public Boolean Exchange(T mine, int timeout, out T yours)
        {
            lock (_threadElem)
            {
                if (_threadElem._message.Equals(default(T)))
                {
                    int lastTime = timeout != Timeout.Infinite ? Environment.TickCount : Timeout.Infinite;
                    try
                    {
                        _threadElem._message = mine;
                        Monitor.Wait(_threadElem, timeout);
                        if (timeout != Timeout.Infinite)
                        {
                            int now = Environment.TickCount;
                            int elapsed = now - lastTime;
                            if (elapsed >= timeout)
                            {
                                yours = _threadElem._message;
                                _threadElem._message = default(T);
                                return false;
                            }
                        }
                    }
                    catch (ThreadInterruptedException)
                    {
                        Thread.CurrentThread.Interrupt();
                        yours = _threadElem._message;
                        _threadElem._message = default(T);
                        throw;
                    }
                    yours = _threadElem._message;
                    _threadElem._message = mine;
                    return true;
                }
                yours = _threadElem._message;
                _threadElem._message = mine;
                Monitor.Pulse(_threadElem);
                return true;
            }
        }
    }
}