#ifndef LINUX_MINIX_FS_H
#define LINUX_MINIX_FS_H
#include "Types.h"
/*
 * The minix filesystem constants/structures
 */


typedef struct minix_dir_entry{
	U16 inode;
	U8 name[30];
}Directory;
/**
 * The partition table is located at the end of the master boot record, 
 * which occupies the first physical sector of the hard disk. The table 
 * starts at offset 446 and has a length of 4 x 16 bytes.
 * Each of these 16 bytes describes one disk partition, in the 
 * following way:
 * A			:Active partition: 0x80; else: 0x00 [Note: at most one partition active at any time.]
 * CHS First	:CHS absolute address of first sector in partition
 * T			:Partition type
 * CHS Last		:CHS absolute address of last sector in partition
 * LBA Start	:Linear absolute address of first sector in partition
 * LBA Count	:Total number of sectors in partition
 * */
typedef struct{
	U8	Active		;
	U8  CHS_First[3];
	U8  Type		;
	U8  CHS_Last[3]	;
	U32 LBA_Start	;
	U32 LBA_Count	;
}Partition;

typedef struct{
	U32			CurrentActivePartition;
	Partition	Table;
}PartitionTable;
//----------------------------------------------------------------------
//INFO
//Partition Start/Boot Block	: 22192128
//SuperBlock				  	: 22192128 + 1024 		= 22193152
//Inodes Bitmap			  		: 22193152 + 1024 		= 22194176
//Zone Bitmap				  	: 22194176 + 2 * 1024 	= 22196224
//Inodes					  	: 22196224 + 6 * 1024 	= 22202368
//Data					  		: i_zone[0] * ZONE_SIZE


/**
 * Sector size	= 	512  bytes
 * Block  size 	= 	1024 bytes
 * Zone	 size	=	1024 bytes
 * */
#define SECTOR				1
#define SECTOR_SIZE			512
#define ZONE				2 * SECTOR
#define ZONE_SIZE			ZONE * SECTOR_SIZE
/**
 * The boot block takes exactly one block and contains the volume boot 
 * record.
 * */
#define BOOT_BLOCK				ZONE
#define BOOT_BLOCK_SIZE	 		ZONE_SIZE
#define BOOT_BLOCK_LOCATION	0
/**
 * The superblock takes another block and contains information about the
 * layout of the file system.
 * */
#define SUPER_BLOCK				ZONE
#define SUPER_BLOCK_SIZE		ZONE_SIZE
#define SUPER_BLOCK_LOCATION	0 + BOOT_BLOCK

#define INODES_BITMAP_LOCATION	0 + BOOT_BLOCK + SUPER_BLOCK


/*
 * minix super-block data on disk
 */
typedef struct minix_super_block{
							//: Valores da partição
	U16 s_ninodes;			//: 37d0 			= 14288
	U16 s_nzones;			//: 0000			= 0 
	U16 s_imap_blocks;		//: 0002			= 2
	U16 s_zmap_blocks;		//: 0006			= 6
	U16 s_firstdatazone;	//: 0387			= 903
	U16 s_log_zone_size;	//: 0000			= 0
	U32 s_max_size;			//: 7fff ffff		= 2147483647
	U16 s_magic;			//: 2478			= 2478
	U16 s_state;			//: 0000			= 0
	U32 s_zones;			//: 0000 a758		= 42840
	U8  s_reserved[1000];	//: to fullfill 1 zone = 1024
}SuperBlock;
/**
 * Each i-node in the I-nodes area occupies 64 bytes and is used to hold
 * metadata about one file system entry (file, directory,  ...). 
 * The number of blocks used for i-nodes is decided at format time  and 
 * is stored in the superblock.
 * */
 #define INODE_SIZE		64
 /**
  * The I-nodes bitmap area has one bit per existing i-node and is used 
  * to find free i-nodes in the I-nodes area. 
  * Similarly, the Zone bitmap has one bit for each zone in the Data 
  * area and is used to find free zones in it.
  * */
 /*
 * The new minix inode has all the time entries, as well as
 * long block numbers and a third indirect block (7+1+1+1
 * instead of 7+1+1). Also, some previously 8-bit values are
 * now 16-bit. The inode is now 64 bytes instead of 32.
 */
typedef struct minix2_inode{
	U16 i_mode;
	U16 i_nlinks;
	U16 i_uid;
	U16 i_gid;
	U32 i_size;
	U32 i_atime;
	U32 i_mtime;
	U32 i_ctime;
	U32 i_zone[10];
}INode;

/**
 * The Data area is usually the largest one and holds the contents of 
 * files and directories. Its blocks are grouped into zones, with each 
 * zone representing 2n blocks, for some n >= 0.
 * */


//----------------------------------------------------------------------
/**
 * Table 3 – Second partition block and zone sizes
 * Block size (in bytes) : 1024
 * Zone size (in bytes)  : 1024
 * */


//----------------------------------------------------------------------
//my API
//----------------------------------------------------------------------
#define DISK_ROOT_ADDR				0x0
#define ONE_SECTOR					0x1
#define PARTITION_TABLE_START_ADDR 	446
#define PARTITION_ACTIVE			0x80
#define INODE_BUFFER				16
#define MINIX_PARTITION				1
#define SIZE_OF_BUFFER				1024
U32 Minix_ReadFileDataZone(Partition* partition, INode* imageNode, U8* destination);
void Minix_Start(PartitionTable* table, SuperBlock* super);
U32 Minix_LoadImages(Partition* partition, SuperBlock* superBlock,INode* mbr_buffer);

#endif
