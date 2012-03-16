#include "include_linux_minix_fs.h"
/*
#include <stdio.h>
#include <stdlib.h>
*/

#define DIRECT_NUMBER_OF_ZONES 		7
#define DIRECT_ZONE_START_NUMBER	0


static Directory 		dirbuffer[22];
static char* IMAGE_FOLDER="imagens";

// Source : http://freebsd.active-venture.com/FreeBSD-srctree/newsrc/libkern/strcmp.c.html
int strcmp(s1, s2)
	register const char *s1, *s2;
{
	while (*s1 == *s2++)
		if (*s1++ == 0)
			return (0);
	return (*(const unsigned char *)s1 - *(const unsigned char *)(s2 - 1));
}

/*
U32 ATA_read(U32 LBA, void* buffer, U16 numberOfSectors)
{
	FILE *fp;
	int res;
	LBA=LBA*SECTOR_SIZE;
	//char buffer[1024];
	if((fp = fopen("/home/nac/WorkingArea/LSC/semestre-7/LSC/dsk/lsc-hd63-flat.img", "rb")) == NULL)
	{
		printf("Cannot open file.\n");
		exit(1);
	}

	fseek(fp,LBA,SEEK_SET);
	if ((res= fread(buffer, 1,numberOfSectors*512,fp)) != numberOfSectors*512){
		printf("Error reading file.\n");
		exit(1);
	}


	if( fclose( fp ))
	{
      printf("File close error.\n");
    }
    return 0;
}
*/
static void FS_READ (Partition* partition, U32 firstSector,U32 numberofSectors,void* destination)
{
	U32 address = partition->LBA_Start + firstSector;
	ATA_read(address,destination,numberofSectors);
}

/**
 * The partition table is located at the end of the master boot record, 
 * which occupies the first physical sector of the hard disk.
 * */
void getPartitionTable(PartitionTable* partitionTable)
{
	U8 ActivePartition=0;
	//buffer para conter o 1º sector do disco que contem o MBR
	U8 mbr_buffer[512]; 
	Partition * partition = (Partition*)&mbr_buffer[PARTITION_TABLE_START_ADDR];
	//Lê o primeiro sector do disco
	ATA_read(DISK_ROOT_ADDR,(void*)mbr_buffer,ONE_SECTOR);
	//descobrir qual a partição activa
	for(ActivePartition=0;ActivePartition<4;ActivePartition++){
		if(partition[ActivePartition].Active == PARTITION_ACTIVE){
			partitionTable->CurrentActivePartition=ActivePartition;
			partitionTable->Table = partition[ActivePartition];
		}
	}
}
/**
 * "Carrega" o conteudo do SuperBlock.
 * */
void getSuperBlock(Partition* partition, SuperBlock* superBlock)
{
	FS_READ(partition,SUPER_BLOCK_LOCATION,SUPER_BLOCK,(void*)superBlock);
}
/**
static void showInodeContent(Partition* partition,INode* node)
{
	int i=0;
	U32 addr = node->i_zone[0] * ZONE_SIZE + partition->LBA_Start*SECTOR_SIZE ;
	ATA_read(addr,(void*)&dirbuffer,ZONE);
	for (i=0;i<22;++i){
		if(dirbuffer[i].inode == 0)
			break;
		printf("[%0i] ----> %s\n",dirbuffer[i].inode,dirbuffer[i].name);	
	}
}
*/
static U32 imagesFolder(Partition* partition,INode* rootINode, char* imageFolder)
{
	int i=0;
	FS_READ(partition,rootINode->i_zone[0]*2,ZONE,(void*)&dirbuffer);
	for (i=0;i<32;++i){
		if(dirbuffer[i].inode == 0)
			continue;
		if(strcmp(dirbuffer[i].name,imageFolder) == 0)
		{
			return dirbuffer[i].inode;
		}
	}
	return 0;
}

/**
 * Baseado no  INode Raiz da Directoria de Imagens colecciona os INodes 
 * de cada imagem, devolvendo o numero de imagens que foram coleccionadas
 */
static U32 getImagesINodes(Partition* partition,INode* rootINode,INode* InodeBuffer,U32 baseAddress)
{
	U32 i=0,j=0;
	FS_READ(partition,rootINode->i_zone[0]*2,ZONE,(void*)&dirbuffer);

	INode mbr_buffer[INODE_BUFFER]; 
	FS_READ(partition,baseAddress,ZONE,&mbr_buffer);
	for (i=0;i<32;++i){
		if(dirbuffer[i].inode == 0)
			continue;
		if(strcmp(dirbuffer[i].name,".") != 0 && strcmp(dirbuffer[i].name,"..") != 0)
		{
			//printf("[%i][%i] -> %s \n",j,dirbuffer[i].inode,dirbuffer[i].name);
			InodeBuffer[j]=(INode)mbr_buffer[dirbuffer[i].inode-1];
			++j;
		}
	}
	return j;
}
/**
 * "Carrega" todos os inodes válidos para um array passado por parametro.
 * */
static U32 getINodes(Partition* partition, SuperBlock* superBlock,INode* mbr_buffer)
{
	U32 imageNode=0;
	U32 amt = 0;
	U32 inodeAddr=BOOT_BLOCK + SUPER_BLOCK + superBlock->s_imap_blocks*2 + superBlock->s_zmap_blocks*2;
	FS_READ(partition,inodeAddr,ZONE,(void*)mbr_buffer);

	//Lê o primeiro INode, obtem o id do Inode que contem as imagens
	imageNode = imagesFolder(partition,&mbr_buffer[0],IMAGE_FOLDER);
	if (imageNode == 0 ){return 255;}

	amt=getImagesINodes(partition,&mbr_buffer[imageNode-1],mbr_buffer,inodeAddr);
	return amt;
}

/**
 * Lê a última zona de indirecção.
 * @destinationStart : indica o endereço da primeira posição disponivel
 * do buffer de destino.
 * @baseAddress o endereço base absoluto deste nivel de indirecção. 
 * */
static U32 ReadZone(U8* destinationStart,U32 baseAddress,U32 zone, U32 MaxRead)
{
	U32 idx=0;
	U32 address=0;
	U32 status=0;
	char zoneBuffer[ZONE_SIZE];
	address = baseAddress + zone*ZONE_SIZE;
	ATA_read(address,&zoneBuffer,ZONE);
	
	int * baseBuffer = (int *) &zoneBuffer;
	for(idx=0;idx<MaxRead;++idx)
	{
		if(baseBuffer[idx] == 0) {status=1;break;}
		address = baseAddress + baseBuffer[idx]*ZONE_SIZE;
		ATA_read(address,destinationStart,ZONE);
		destinationStart+=ZONE_SIZE;
	}	
	return status;
}

static U32 DirectZone(U8* destinationStart,U32 baseAddress,INode* imageNode)
{
	U32 address = 0, idx = 0 , status = 0;
	U8* ptr = destinationStart;
	for (idx = DIRECT_ZONE_START_NUMBER;idx< DIRECT_NUMBER_OF_ZONES;++idx)
	{
		if ((U32)(imageNode->i_zone[idx]) == 0){status = 1; break;}
		address = baseAddress + ((U32)imageNode->i_zone[idx])*ZONE_SIZE;
		ATA_read(address,ptr,ZONE);
		ptr+=ZONE_SIZE;
	}
	return status;
}

static U32 IndirectZone(U8* destinationStart,U32 baseAddress,INode* imageNode)
{
	if ((U32)(imageNode->i_zone[7]) == 0) return 1;
	return ReadZone(destinationStart,baseAddress,imageNode->i_zone[7],256);
}

static U32 DoubleIndirectZone(U8* destinationStart,U32 baseAddress,INode* imageNode)
{
	if (imageNode->i_zone[8] == 0) return 1;
	U32 idx=0, address=0, status=0;
	
	address = baseAddress + ((U32)(imageNode->i_zone[8]))*ZONE_SIZE;
	char buffer[ZONE_SIZE];
	ATA_read(address, &buffer,ZONE);
	int * baseBuffer = (int *) &buffer;
	for(idx = 0; idx < 256; idx++){
		//address=baseAddress + baseBuffer[idx]*ZONE_SIZE;
		status = ReadZone(destinationStart,baseAddress,baseBuffer[idx],256);
		destinationStart += ZONE_SIZE;
		if (status){break;}
	}	   
	return status;
}


/**
 * --> One direct zone pointer in an i-node contains the logical address of 
 * a data block, which contains 1K data of the file whose property is 
 * described by the i-node. 
 * -->A single indirect zone pointer contains the logical address of a 
 * single indirect block, which in turn contains 256 logical addresses, 
 * with each of them pointing to a data block containing 1K data of the 
 * file. 
 * -->Double indirect zone pointer works the same way. 
 **/ 
U32 Minix_ReadFileDataZone(Partition* partition, INode* imageNode, U8* destination)
{
	U32 status=0;

	//endereço base
	//U32 dataAddress = imageNode->i_zone[0] * ZONE_SIZE + partition->LBA_Start*SECTOR_SIZE ;
	U32 dataAddress = partition->LBA_Start*SECTOR_SIZE ;

	//Chamar as zonas directas.
	status=DirectZone(destination,dataAddress,imageNode);
	if(status) return status;

	//recolocar o ponteiro
	destination += 7 * ZONE_SIZE;

	//recalcular o endereço base
	//dataAddress=imageNode->i_zone[7] * ZONE_SIZE + partition->LBA_Start*SECTOR_SIZE ;

	//Chamar as zonas de 1 indirecção.
	status=IndirectZone(destination,dataAddress,imageNode);
	if(status) return status;

	//recolocar o ponteiro
	destination+= 256 * ZONE_SIZE;

	//recalcular o endereço base
	//dataAddress=imageNode->i_zone[8] * ZONE_SIZE + partition->LBA_Start*SECTOR_SIZE ;

	//Chamar as zonas de 2 indirecção.
	status=DoubleIndirectZone(destination,dataAddress,imageNode);
	return status;
	
}


void Minix_Start(PartitionTable* table, SuperBlock* super)
{
	getPartitionTable(table);
	getSuperBlock(&(table->Table),super);
}

U32 Minix_LoadImages(Partition* partition, SuperBlock* superBlock,INode* mbr_buffer)
{
	return getINodes(partition,superBlock,mbr_buffer);
}

/*
int main(int argc, char **argv)
{
PartitionTable table;
SuperBlock superblock;
INode	inodeBuffer[INODE_BUFFER];
	Minix_Start(&table,&superblock);
	U32 imageNbr=0;
	//idx=0, status;
	//Obtem os inodes das Imagens
	imageNbr = Minix_LoadImages(&(table.Table),&superblock,inodeBuffer);
	printf("Numero de imagens: %i\n", imageNbr);
	return 0;
}
*/

