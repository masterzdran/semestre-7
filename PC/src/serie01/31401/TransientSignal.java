/**
 * 
 */
package serie01;

import java.util.LinkedList;

/**
 * 
 * @author Nuno Cancelo
 * Implemente  em  Java  o  sincronizador  transient  signal,  com  base  na  classe  TransientSignal
 * que define as opera��es await, signalOne e signalAll. 
 * A chamada a await � SEMPRE bloqueante, retornando  true quando tiver ocorrido uma  sinaliza��o expl�cita 
 * (via signalOne ou signalAll) ou  false  se  ocorrer  timeout.  
 * A  invoca��o  de  signalOne  liberta  uma  das  threads  bloqueadas,  se existir alguma. 
 * A invoca��o de signalAll liberta todas as threads bloqueadas nesse momento. 
 * Caso n�o existam threads bloqueadas, as chamadas a signalOne ou signalAll n�o produzem efeito. 
 * Na implementa��o  tenha  em  considera��o  que  n�o  �  necess�rio  desbloquear  as  threads  por  ordem  de chegada.
 */
public final class TransientSignal {
	private static class EntityWorker{
		public int status= 0;
	}
	
	private final LinkedList<EntityWorker> _transientSignal ;
	
	public TransientSignal(){
		_transientSignal = new LinkedList<TransientSignal.EntityWorker>();
	}
	
	
	public boolean await(){
		return await(0);
	}
	public boolean await(long timeoutMiliSeconts){
		synchronized (_transientSignal) {
			EntityWorker e = new EntityWorker();
			e.status = 1;
			_transientSignal.add(e);
			long endTime = System.currentTimeMillis() + timeoutMiliSeconts; 
			try {
				_transientSignal.wait(timeoutMiliSeconts);
			} catch (InterruptedException e1) {
				if (!_transientSignal.isEmpty()){
					EntityWorker tmp = _transientSignal.remove();
					tmp.status=0;
					_transientSignal.notify();
				}
			}
			if (endTime - System.currentTimeMillis()  > 0) { //n�o ocorreu timeout
				return true;
			}
			return false;	
		}
		
	}
	public void signalOne(){
		synchronized (_transientSignal) {
			if (!_transientSignal.isEmpty()){
				EntityWorker e = _transientSignal.remove();
				e.status=0;
				_transientSignal.notify();
			}
		}
	}
	public void signalAll(){
		synchronized (_transientSignal) {
			if (!_transientSignal.isEmpty()){
				while (!_transientSignal.isEmpty()){
					EntityWorker e = _transientSignal.remove();
					e.status=0;
				}
				_transientSignal.notifyAll();
			}
		}
	}
	
	
	
}
