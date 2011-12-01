/*
 * Implemente em Java o sincronizador transient signal, com base na classe
 * TransientSignal que define as opera��es await, signalOne e signalAll.
 * A chamada a await � SEMPRE bloqueante, retornando true quando tiver
 * ocorrido uma sinaliza��o expl�cita (via signalOne ou signalAll) ou
 * false se ocorrer timeout. A invoca��o de signalOne liberta uma das
 * threads bloqueadas, se existir alguma. A invoca��o de signalAll liberta
 * todas as threads bloqueadas nesse momento. Caso n�o existam threads
 * bloqueadas, as chamadas a signalOne ou signalAll n�o produzem efeito.
 * Na implementa��o tenha em considera��o que n�o � necess�rio desbloquear
 * as threads por ordem de chegada.
 */
public final class TranscientSignal {
	
	private final Object _lock;
	
	public TranscientSignal(){
		_lock = new Object();
	}
	
	public boolean await(long timeout) throws InterruptedException
	{
		synchronized (_lock)
		{
			long lastTime = timeout == 0 ? 0 : System.currentTimeMillis();
			try
			{
				_lock.wait(timeout);
				if (timeout != 0)
				{
					long now = System.currentTimeMillis();
					long elapsed = now - lastTime;
					if (elapsed >= timeout) return false;
				}
				return true;
			}
			catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				throw ie;
			}
		}
	}
	
	public void signalOne()
	{
		synchronized (_lock) {
			_lock.notify();
		}
	}
	
	public void signalAll()
	{
		synchronized (_lock) {
			_lock.notifyAll();
		}
	}
	

}
