#include "minix_fs.h"
// Source : http://freebsd.active-venture.com/FreeBSD-srctree/newsrc/libkern/strcmp.c.html
int strcmp(s1, s2)
	register const char *s1, *s2;
{
	while (*s1 == *s2++)
		if (*s1++ == 0)
			return (0);
	return (*(const unsigned char *)s1 - *(const unsigned char *)(s2 - 1));
}

/**
 * Lê a tabela de partições e preenche uma estrutura com os dados da partição activa
 * */
U32 readPartition(PARTITION* partition, unsigned partitionNbr){
	char mbr[512]; 
	PARTITION_TABLE * partitionTable = (PARTITION_TABLE*)&mbr[446];
	ATA_read(((U16*)mbr), 0, 1);
   partition->fileDescriptor = partitionNbr;
	partition->partitionTable = partitionTable[partitionNbr];
	return 0;
}


U32 readSectors(PARTITION * partition, U32 firstSector, U32 numberSectors, void * destination)
{
	int addr = ( (partition->partitionTable.lba_start) + firstSector*2 );
	return ATA_read(destination, addr, numberSectors);
}

int getNodes( PARTITION * partition, void * buffer ){	
	SUPERBLOCK_TABLE superBlockTable;
   readSectors(partition, SUPER_BLOCK, 2, (void *)(&superBlockTable));
	int inodes = I_NODES_BITMAP + superBlockTable.s_imap_blocks + superBlockTable.s_zmap_blocks;	
	return  readSectors(partition, inodes, 2, (void*)buffer);
}

static void getDirectory(PARTITION * part, char * name, char* tempBuffer,SimpleDir* directoryDestination){
	int i;
	getNodes(part, (void *)tempBuffer);
	iNODE * node = (iNODE *)tempBuffer; 
	readSectors(part, node->i_zone[0], 2, (void*) tempBuffer); 
	DIR_ENTRY * entries = (DIR_ENTRY *) tempBuffer; 
	for(i = 2; strcmp(entries[i].name, name) != 0 ; i++);				  
	i = entries[i].inode; 
	getNodes(part, (void *)tempBuffer); 
	node = ((iNODE*) tempBuffer);
	node = &node[i-1];
	readSectors(part, node->i_zone[0], 2, (void*) tempBuffer);
   directoryDestination->DirectoryEntry = (DIR_ENTRY *) tempBuffer;
}

int getDirectoryContentLength(PARTITION * part, char * name,char * buffer, SimpleDir* directory){
	int i;
   getDirectory(part, name, buffer, directory);

   for(i = 2; directory->DirectoryEntry[i].inode != 0; i++);
   directory->ContentLength = i-2;
	return directory->ContentLength;
}

int getDirectoryContent(PARTITION * part, char * name, iNODE * destination, SimpleDir* directory){
	int i;
	char other_buffer[BUFFER_SIZE];
	getNodes(part, (void *)other_buffer); 
	iNODE * nodes = (iNODE*) other_buffer;
	for(i = 2; directory->DirectoryEntry[i].inode != 0 ; i++) {
		destination[(i-2)] = nodes[((directory->DirectoryEntry[i].inode) - 1)];
	}
	
   directory->ContentLength = i-2;
	return directory->ContentLength;
}



static void storeDataZone(int * storage, int storageSize,iNODE * node, PARTITION * partition)
{
   U32 writeByteIdx = 0, idx = 0;
   
   //Direct Blocks
	for(idx = 0; idx<7; idx++, writeByteIdx++){
		storage[writeByteIdx] = node->i_zone[idx];
	}	
   //Indirect Blocks
   idx=0;
	char buffer[BUFFER_SIZE];
	readSectors(partition, node->i_zone[7], 2, (void*) buffer);
	for(; idx<256; idx++, writeByteIdx++){
		storage[writeByteIdx] = ((int *) &buffer)[idx];
	}
   
   //Double Indirect Blocks
   idx=0;int  index=0;
	char anotherBuffer[BUFFER_SIZE];
	readSectors(partition, node->i_zone[8], 2, (void*) buffer);
	int * pointer = (int *) &buffer;	
	for(idx = 0; idx < 256; idx++){
		readSectors(partition, pointer[idx], 2, (void*) anotherBuffer);
		int * anotherPointer = (int *) &anotherBuffer;
		for(index = 0; index < 256 && writeByteIdx<storageSize; index++, writeByteIdx++) storage[writeByteIdx] = anotherPointer[index]; 
	}	   

}

void readFile(PARTITION * partition, iNODE * file_node, void * destination){	
	int n_zones = file_node->i_size/1024+1;
	int data_zones[n_zones];
   storeDataZone(data_zones,n_zones, file_node, partition);
	int i;
	for( i=0 ; i<n_zones ; ++i )
	{
		readSectors(partition, data_zones[i], SECTORS, destination);
		destination += SECTORS*512;
	}
}
