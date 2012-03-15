/*
#=======================================================================
# LSC   - Laboratorio de Sistemas Computacionais
#-----------------------------------------------------------------------
# Turma:	LI51N
# Semestre:	Inverno 2011/2012
# Data:		Fevereiro/2011
#-----------------------------------------------------------------------
# Nome: 	   Nuno Cancelo
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
#define BYTE 8

#define FREQUENCY 1193180  
#define MILI		1000
#define SECOND		1
#define DEFAULT_MS	10

void Timer_start()
{
	int timerSetup = (FREQUENCY/MILI)*DEFAULT_MS;
	outb(ACTION,__CONTROL__);
	outb( timerSetup         & LOW_BYTE_MASK,  __SELECT_COUNTER_0__);        
	outb((timerSetup >> BYTE)& LOW_BYTE_MASK,  __SELECT_COUNTER_0__);  
}

static S32 Timer_elapsed(int last_read)
{
	outb(0,__CONTROL__);                   
	int timer_read  = inb(__SELECT_COUNTER_0__); 
	    timer_read += inb(__SELECT_COUNTER_0__)<< BYTE;
	return timer_read -  last_read;  
}

void Timer_delay(long elapse)
{
    U32 time;
    time = Timer_elapsed(0);
    while(Timer_elapsed(time)<= elapse);
}
