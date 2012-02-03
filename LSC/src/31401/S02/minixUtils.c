#include "minixUtils.h"

static short open = 0;

int read(int fd, char * buf, int n)
{
	if(open==0)
	{
		//openPartition();
	}
	//readSectors();
}

int write(int fd, char * buf, int n)
{
	if(open==0)
	{
		//openPartition();
	}
	//writeSectors();
}

