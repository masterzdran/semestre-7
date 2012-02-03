#ifndef __DISK_IO__
#define __DISK_IO__

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

int openPartition(PARTITION * part, DISK disk , unsigned npart);
int readSectors(PARTITION * part, u32 lba, u32 nsects, void * dest);
int closePartition(PARTITION * part);

#endif
