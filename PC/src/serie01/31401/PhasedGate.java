package serie01;

import java.util.LinkedList;

/**
 * 
 * @author Nuno Cancelo
 *
 * Implemente  em  Java  o  sincronizador  phased  gate,  com  base  na  classe  PhasedGate  que  define  a
 * operação de wait. Esta operação é bloqueante até que seja chamada pelas N threads participantes (o valor de N é especificado no construtor).
 *  A última thread a invocar wait não fica bloqueada, sendo libertadas as restantes. 
 *  O sincronizador tem ainda a operação RemoveParticipant, que serve para remover  uma  unidade  ao  número  de  participantes.  
 *  Note  que  as  instâncias  de  PhasedGate  são  de utilização única. 
 */
public final class PhasedGate  {
	private static class EntityWork{public int number;}
	private int _instanceNbr;
	private final  LinkedList<EntityWork> _phasedGate;

	public PhasedGate(int n){
		_instanceNbr = n;
		_phasedGate = new LinkedList<PhasedGate.EntityWork>();
	}

	public void Wait(){

		Wait(0 /*Infinity*/);
	}
	
	public void Wait(int timeoutMiliSeconds){
		synchronized (_phasedGate) {
			EntityWork e = new EntityWork();
			e.number = _instanceNbr;
			--_instanceNbr;
			_phasedGate.addLast(e);
			if (_instanceNbr > 0 ){
				try {
					_phasedGate.wait(timeoutMiliSeconds);
				} catch (InterruptedException e1) {
					if (_instanceNbr == 0)
						_phasedGate.notifyAll();
				}
			}else{
				_phasedGate.notifyAll();
			}			
		}		
	}
	public void RemoveParticipant(){
		synchronized (_phasedGate) {
			_instanceNbr++;
			EntityWork e = _phasedGate.removeLast();
			e.number=0;
		}
	}


}
