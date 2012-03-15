/*
#=======================================================================
# LSC   - Laboratorio de Sistemas Computacionais
#-----------------------------------------------------------------------
# Turma:	LI51N
# Semestre:	Inverno 2011/2012
# Data:		Fevereiro/2011
#-----------------------------------------------------------------------
# Nome: 	   Nuno Cancelo
# Numero:	31401
#-----------------------------------------------------------------------
# Nome:		Nuno Sousa
# Numero:	33595
#-----------------------------------------------------------------------
# LEIC  - Licenciatura em Engenharia Informática e Computadores
# DEETC - Dep. de Eng. Electrónica e Telecomunicações e Computadores
# ISEL  - Instituto Superior de Engenharia de Lisboa
#=====================================================================
*/
//#include "minix_fs.h"
#include "include_linux_minix_fs.h"
#include "timer.h"
#include "BMP.h"
#include "Types.h"

char * const image_ptr = (char*) 0x600000;
//iNODE * const images = (iNODE *) 0x400000;

//#define IMAGES_HOME  "image"
//static PARTITION part;
//static int total;

//static PartitionTable table;
//static SuperBlock superblock;
//static INode	inodeBuffer[INODE_BUFFER];

void lsc_startup()
{
	//Minix_Start(&table,&superblock);
}
void lsc_run()
{
	PartitionTable table;
	SuperBlock superblock;
	INode	inodeBuffer[INODE_BUFFER];
	U32 imageNbr=0, idx=0, status;
	Minix_Start(&table,&superblock);
	//Obtem os inodes das Imagens
	imageNbr = Minix_LoadImages(&(table.Table),&superblock,inodeBuffer);
	RGBPixel green = {0,16,0};
	RGBPixel blue = {16,0,0};
	RGBPixel red = {0,0,16};
	
	while(1){
		if (imageNbr == 255){
			DisplayColor(green);
			Timer_delay(1000);
		}else{
			DisplayColor(red);
			Timer_delay(1000);
		}
		
		for(idx=0;idx<imageNbr;++idx)
		{
			status = Minix_ReadFileDataZone(&(table.Table),&(inodeBuffer[idx]),image_ptr);
			if (status)
			{
				DisplayColor(blue);
				Timer_delay(1000);
			}
			DisplayBMPImage(image_ptr);
		}
		
	}
}
/*
void lsc_startup()
{	
   SimpleDir directory;
   char buffer[BUFFER_SIZE];
	readPartition(&part, 1);
   
	total = getDirectoryContentLength(&part, IMAGES_HOME,buffer,&directory);
	getDirectoryContent(&part, IMAGES_HOME, images,&directory);	
}

void lsc_run()
{
   int idx = 0;
   for(idx = 0 ; idx + 1 ;idx = (idx + 1) % total){
      readFile(&part, &images[idx], (void*) image_ptr);
      DisplayBMPImage(image_ptr);
      Timer_delay(1000);
   }
}
*/
void lsc_main() {
	lsc_startup();
	lsc_run();
}

