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

U32 openPartition(PARTITION * part, unsigned npart)
{
	char mbr[512]; 
	PARTITION_TABLE * partTable = (PARTITION_TABLE*)&mbr[446];
	ATA_read(((U16*)mbr), 0, 1);
	part->pte = partTable[npart];
	return 0;
}

U32 readSectors(PARTITION * part, U32 fsect, U32 nsects, void * dest)
{
	int addr = ( (part->pte.lba_start) + fsect*2 );
	return ATA_read(dest, addr, nsects);
}

int getSuperBlock(PARTITION * part, SUPERBLOCK_TABLE * super_table){
	return readSectors(part, SUPER_BLOCK, 2, (void *)super_table);
}

int getNodes( PARTITION * part, void * buffer ){	
	SUPERBLOCK_TABLE super_table;
	getSuperBlock(part, &super_table);
	int inodes = I_NODES_BITMAP + super_table.s_imap_blocks + super_table.s_zmap_blocks;	
	return  readSectors(part, inodes, 2, (void*)buffer);
}

int getDirectoryContentLength(PARTITION * part, char * name){
	int i;
	char buffer[BUFFER_SIZE];
	getNodes(part, (void *)buffer);
	iNODE * node = (iNODE *)buffer; 
	readSectors(part, node->i_zone[0], 2, (void*) buffer); 
	DIR_ENTRY * entries = (DIR_ENTRY *) buffer; 
	for(i = 2; strcmp(entries[i].name, name) != 0 ; i++);				  
	i = entries[i].inode; 
	getNodes(part, (void *)buffer); 
	node = ((iNODE*) buffer);
	node = &node[i-1];
	readSectors(part, node->i_zone[0], 2, (void*) buffer);
	entries = (DIR_ENTRY *) buffer;
	for(i = 2; entries[i].inode != 0; i++) {}
	return i-2;
}

int getDirectoryContent(PARTITION * part, char * name, iNODE * result){
	int i;
	char buffer[BUFFER_SIZE];

	getNodes(part, (void *)buffer);
	iNODE * node = (iNODE *)buffer; 
	
	readSectors(part, node->i_zone[0], 2, (void*) buffer); 
	DIR_ENTRY * entries = (DIR_ENTRY *) buffer; 
	for(i = 2; strcmp(entries[i].name, name) != 0 ; i++);				  
	i = entries[i].inode; 
   
	getNodes(part, (void *)buffer); 
	node = ((iNODE*) buffer);
	node = &node[i-1];
	
	readSectors(part, node->i_zone[0], 2, (void*) buffer);
	entries = (DIR_ENTRY *) buffer;
	
	char other_buffer[BUFFER_SIZE];
	getNodes(part, (void *)other_buffer); 
	
	iNODE * nodes = (iNODE*) other_buffer;
	for(i = 2; entries[i].inode != 0 ; i++) {
		result[(i-2)] = nodes[((entries[i].inode) - 1)];
	}
	
	return i-2;	
}

void storeDataZonesDirect(int * storage, iNODE * node){
	int i, stIdx = 0;
	for(i = 0; i<7; i++, stIdx++){
		storage[stIdx] = node->i_zone[i];
	}	
}

void storeDataZones1Indirect(int * storage, iNODE * node, PARTITION * part){
	int idx = 7, i=0;
	char buffer[BUFFER_SIZE];
	
	readSectors(part, node->i_zone[7], 2, (void*) buffer);
	for(; i<256; i++, idx++){
		storage[idx] = ((int *) &buffer)[i];
	}
}

void storeDataZones2Indirect(int * storage, iNODE * node, PARTITION * part){
	int idx = 263, i=0, j=0, n_zones=(node->i_size/1024)+1;
	char buffer[BUFFER_SIZE];
	char buffer0[BUFFER_SIZE];
	readSectors(part, node->i_zone[8], 2, (void*) buffer);
	int * pointers = (int *) &buffer;	
	for(i = 0; i < 256; i++){
		readSectors(part, pointers[i], 2, (void*) buffer0);
		int * pointers0 = (int *) &buffer0;
		for(j = 0; j < 256 && idx<n_zones; j++, idx++) storage[idx] = pointers0[j]; 
	}	
}

void readFile(PARTITION * part, iNODE * file_node, void * destination){	
	int n_zones = file_node->i_size/1024+1;
	int data_zones[n_zones];
	storeDataZonesDirect(data_zones, file_node); 
	storeDataZones1Indirect(data_zones, file_node, part);
	storeDataZones2Indirect(data_zones, file_node, part); 
	char * file_data = (char *)destination;
	int i;
	for( i=0 ; i<n_zones ; ++i )
	{
		readSectors(part, data_zones[i], SECTORS, (void*)file_data);
		file_data += SECTORS*512;
	}
}
