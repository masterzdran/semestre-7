#include "include_linux_minix_fs.h"
//#include <stdio.h>
//#include <stdlib.h>




static Directory 		dirbuffer[22];
static const char* IMAGE_FOLDER="image";

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
static U32 BASE_ADDR;
U32 ATA_read(U32 LBA, void* buffer, U16 numberOfSectors)
{
	FILE *fp;
	int res;
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
* */
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
	//Calcula o endereço do SuperBlock
	U32 superBlockAddr = partition->LBA_Start*SECTOR_SIZE + SUPER_BLOCK_LOCATION * SECTOR_SIZE;
	
	//Lê o bloco do SuperBlock, da partição correcta
	ATA_read(superBlockAddr,(void*)superBlock,SUPER_BLOCK);
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
static U32 imagesFolder(Partition* partition,INode* rootINode, const char* imageFolder)
{
	int i=0;
	U32 addr = rootINode->i_zone[0] * ZONE_SIZE + partition->LBA_Start*SECTOR_SIZE ;
	ATA_read(addr,(void*)&dirbuffer,ZONE);
	for (i=0;i<22;++i){
		if(dirbuffer[i].inode == 0)
			break;
		if(strcmp(dirbuffer[i].name,imageFolder) == 0)
		{
			//TODO: Delete Printf
			//printf("Esta é a pasta de imagens:[%0i][%s]\n",dirbuffer[i].inode,dirbuffer[i].name);
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
	U32 addr = rootINode->i_zone[0] * ZONE_SIZE + partition->LBA_Start*SECTOR_SIZE ;
	ATA_read(addr,(void*)&dirbuffer,ZONE);
	U32 address;
	INode mbr_buffer[INODE_BUFFER]; 
	
	for (i=0;i<22;++i){
		if(dirbuffer[i].inode == 0)
			break;
		if(strcmp(dirbuffer[i].name,".") != 0 && strcmp(dirbuffer[i].name,"..") != 0)
		{
			address = baseAddress + (dirbuffer[i].inode-1)*INODE_SIZE;
			//TODO: Delete Printf
			//printf("Esta é uma imagem:[%0i][%s][%i]\n",dirbuffer[i].inode,dirbuffer[i].name,address);
			ATA_read(address,(void*)mbr_buffer,ZONE);
			InodeBuffer[j]=(INode)mbr_buffer[0];
			++j;
				//TODO: Delete Printf
				//printf("\t\t SIZE: %i\n",InodeBuffer[j].i_size);
		}
	}
	return j;
}
/**
 * "Carrega" todos os inodes válidos para um array passado por parametro.
 * */
static U32 getINodes(Partition* partition, SuperBlock* superBlock,INode* mbr_buffer){
	U32 imageNode=0;
	U32 amt = 0;
	
	//Calcula o endereço para o primeiro inode
	U32 addr1 = partition->LBA_Start*SECTOR_SIZE + BOOT_BLOCK_SIZE + SUPER_BLOCK_SIZE;
	U32 addr2 = superBlock->s_imap_blocks*ZONE_SIZE + superBlock->s_zmap_blocks* ZONE_SIZE;
	U32 inodeAddr = addr1 + addr2;
	
	//Lê o primeiro INode, obtem o id do Inode que contem as imagens
	ATA_read(inodeAddr,(void*)mbr_buffer,ZONE);
	imageNode = imagesFolder(partition,&mbr_buffer[0],IMAGE_FOLDER);
	//TODO: Delete this block
	/*if (imageNode == 0){
		printf("Não existe directoria de imagens %s",IMAGE_FOLDER);
		exit(1);
	}*/

	//Calculo do endereço que contém o INode raiz das imagens
	U32 address = inodeAddr + (imageNode-1)*INODE_SIZE;
	//TODO: Delete Printf
	//printf("[ %i ][ %i ][ %i ] <--- \n", address , inodeAddr,INODE_SIZE);
	ATA_read(address,(void*)mbr_buffer,ZONE);
	amt=getImagesINodes(partition,&mbr_buffer[0],mbr_buffer,inodeAddr);
	return amt;
}




/**
 * Lê a última zona de indirecção.
 * @destinationStart : indica o endereço da primeira posição disponivel
 * do buffer de destino.
 * @baseAddress o endereço base absoluto deste nivel de indirecção. 
 * */

static U32 ReadZone(U8* destinationStart,U32 baseAddress, U32 MaxRead)
{
	U32 idx=0;
	U32 address=0;
	U32 status=0;
	char zoneBuffer[ZONE_SIZE];

	ATA_read(baseAddress,&zoneBuffer,ZONE);
	for(idx=0;idx<MaxRead;++idx)
	{
		if(zoneBuffer[idx] == 0) {status=1;break;}
		address = baseAddress + zoneBuffer[idx]*ZONE_SIZE;
		ATA_read(address,destinationStart,ZONE);
		destinationStart+=ZONE_SIZE;
	}	
	return status;
}

static U32 DirectZone(U8* destinationStart,U32 baseAddress)
{
	//?!?!?Sim eu sei.
	return ReadZone(destinationStart,baseAddress,7);
}

static U32 IndirectZone(U8* destinationStart,U32 baseAddress)
{
	//?!?!?Sim eu sei.
	return ReadZone(destinationStart,baseAddress,256);
}

static U32 DoubleIndirectZone(U8* destinationStart,U32 baseAddress)
{
	int idx=0,address=baseAddress;
	U32 status=0;
	char buffer[ZONE_SIZE];
	ATA_read(address, &buffer,ZONE);
	int * baseBuffer = (int *) &buffer;
	for(idx = 0; idx < 256; idx++){
		address=baseAddress + baseBuffer[idx]*ZONE_SIZE;
		status = IndirectZone(destinationStart,address);
		destinationStart+=(idx+1)*ZONE_SIZE;
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
	//TODO: Delete printf
	//printf("\t %i\n",imageNode->i_size);
	//endereço base
	U32 dataAddress = imageNode->i_zone[0] * ZONE_SIZE + partition->LBA_Start*SECTOR_SIZE ;
	//Chamar as zonas directas. Falta testar retorno.
	status=DirectZone(destination,dataAddress);
	if(status) return status;
	//recolocar o ponteiro
	destination+= 7 * ZONE_SIZE;
	//recalcular o endereço base
	dataAddress=imageNode->i_zone[7] * ZONE_SIZE + partition->LBA_Start*SECTOR_SIZE ;
	//Chamar as zonas de 1 indirecção. Falta testar retorno.
	status=IndirectZone(destination,dataAddress);
	if(status) return status;
	//recolocar o ponteiro
	destination+= 256 * ZONE_SIZE;
	//recalcular o endereço base
	dataAddress=imageNode->i_zone[8] * ZONE_SIZE + partition->LBA_Start*SECTOR_SIZE ;
	//Chamar as zonas de 2 indirecção. Falta testar retorno.
	status=DoubleIndirectZone(destination,dataAddress);
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


