/*
#=======================================================================
# LSC   - Laboratorio de Sistemas Computacionais
#-----------------------------------------------------------------------
# Turma:	LI51N
# Semestre:	Inverno 2011/2012
# Data:		Fevereiro/2011
#-----------------------------------------------------------------------
# Nome: 	Nuno Cancelo
# Numero:	31401
#-----------------------------------------------------------------------
# Nome:		Nuno Sousa
# Numero:	33595
#-----------------------------------------------------------------------
# LEIC  - Licenciatura em Engenharia Informática e Computadores
# DEETC - Dep. de Eng. Electrónica e Telecomunicações e Computadores
# ISEL  - Instituto Superior de Engenharia de Lisboa
#=====================================================================
*/
#include "timer.h"
#include "io.h"
static int lastCount = 0;
static void Timer_reset(unsigned int milis)
{
	outb( milis    & LOW_BYTE_MASK,  __SELECT_COUNTER_0__);        
	outb((milis>>8)& LOW_BYTE_MASK,  __SELECT_COUNTER_0__);   
}

void Timer_start()
{
	outb(ACTION,__CONTROL__);
	Timer_reset(RESET_VALUE);
}

static unsigned int Timer_read()
{
	outb(0,__CONTROL__);                   
	int data = inb(__SELECT_COUNTER_0__); 
	data += inb(__SELECT_COUNTER_0__)<<4;
	return data;
}

static unsigned int Timer_cycles()
{
	int aux = Timer_read();
	int res = lastCount - aux;
	lastCount = aux;
	return 	( res ) < 0 ? 1 : 0;
}

void Timer_delay(long milis)
{
	if(milis<=10) return;
	long total_cycles = (milis+5) / 10; 
	while(total_cycles>0)
	{
		if(Timer_cycles()>0)total_cycles -=1 ;
	}
}
