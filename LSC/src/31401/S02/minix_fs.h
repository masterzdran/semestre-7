#ifndef __MINIX_FS_H__
#define __MINIX_FS_H__

#include "ata.h"

static const int BUFFER_SIZE = 1024;
static const int SECTORS = 2;

enum{
	BOOT = 0,
	SUPER_BLOCK = 1, 
	I_NODES_BITMAP = 2
};

typedef struct partition_table
{
	char active;
	char CHSfirst[3];
	char type;
	char CHSlast[3];
	int lba_start;
	int lba_count;
} PARTITION_TABLE;
//Source : http://www.cs.fsu.edu/~baker/devices/lxr/http/source/linux/include/linux/minix_fs.h
typedef struct minix_super_block { 
	short int s_ninodes;
	short int s_nzones;
	short int s_imap_blocks;
	short int s_zmap_blocks;
	short int s_firstdatazone;
	short int s_log_zone_size;
	int s_max_size;
	short int s_magic;
	short int s_state;
	int s_zones;
	char reserved[1000];
} SUPERBLOCK_TABLE;

typedef struct minix2_inode { 
	short int i_mode;
	short int i_nlinks;
	short int i_uid;
	short int i_gid;
	int i_size;
	int i_atime;
	int i_mtime;
	int i_ctime;
	int i_zone[10];
} iNODE;

typedef struct minix_dir_entry {
	short int inode;
	char name[30];
} DIR_ENTRY;


typedef struct part {
	int fileDescriptor;
	PARTITION_TABLE partitionTable;
} PARTITION;

typedef struct{
   U32 ContentLength;
   DIR_ENTRY* DirectoryEntry; 
}SimpleDir;
/*
U32 readPartition(PARTITION* partition, unsigned partitionNbr);
void getDirectoryContentLength(PARTITION * part, char * name, SimpleDir* directoryDestination);
void getDirectoryContent(PARTITION * part, char * name, iNODE * destination, SimpleDir* directoryDestination);
void readFile(PARTITION * partition, iNODE * file_node, void * destination);
*/

U32 readPartition(PARTITION* partition, unsigned partitionNbr);
int getDirectoryContentLength(PARTITION * part, char * name,char * buffer, SimpleDir* directory)
int getDirectoryContent(PARTITION * part, char * name, iNODE * destination, SimpleDir* directory);
void readFile(PARTITION * partition, iNODE * file_node, void * destination);

#endif
