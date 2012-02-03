#include "counter.h"
#include "io.h"

int lastCount = 0;

void resetCounter(int milis)
{
	outb(milis&0xFF,0x40); // set initial low value to counter 0
	outb((milis>>8)&0xFF,0x40); // set initial high value to counter 0
}

void startCounter() // using counter 0
{
	// set control byte
	outb(0x34,0x43);
	
	// set initial value
	//resetCounter(0x3E8); // first calculated value... ??
	//resetCounter(0x2E9C); // suposed correct value
	resetCounter(0x9E0); // best aproximate value
}

int readCounter()
{
	outb(0,0x43); // set read with latch mode
	int data = inb(0x40); // read low byte
	data += inb(0x40)<<4; // read high byte
	return data;
}

int cycleComplete()
{
	int aux = readCounter();
	int res = lastCount - aux;
	lastCount = aux;
	return 	( res ) < 0 ? 1 : 0;
}

void delay(long milis)
{
	if(milis<=10) return;
	
	// each cycle takes 10 milis to complete... is a 10 cycle group
	long total_cycles = (milis+5) / 10; 
	while(total_cycles>0)
	{
		//micro sleep?
		if(cycleComplete()>0)total_cycles -=1 ;
	}
}
