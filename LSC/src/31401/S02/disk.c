#include "disk.h"

// http://wiki.osdev.org/ATA_PIO_Mode

typedef struct partition_table
{
	char active;
	char CHSfirst[3];
	char type;
	char CHSlast[3];
	u32 lba_start;
	u32 lba_count;
} PARTITION_TABLE;

typedef struct part 
{
	u32 fd;
	PARTITION_TABLE * pte;
} PARTITION;

typedef char * DISK;

int openPartition(PARTITION * part, DISK disk , unsigned npart)
{
	char mbr[512];
	PARTITION_TABLE * partTable = (PARTITION_TABLE*)&mbr[446];
	
	part->fd = open(disk, 0); // 0_RDONLY = 0
	read(part->fd, mbr, 512);
	part->pte = partTable[npart];
	
	return 0;
}

int readSectors(PARTITION * part, u32 lba, u23 nsects, void * dest)
{
	read( part->fd, dest, (nsects*512) );
	return 0;
}

int closePartition(PARTITION * part)
{
	close(part->fd);
	return 0;
}

