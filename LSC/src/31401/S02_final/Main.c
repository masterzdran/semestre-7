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
void lsc_main() {
   Timer_start();
	PartitionTable table;
	SuperBlock superblock;
	INode	inodeBuffer[INODE_BUFFER];
   //U8 image_ptr[1440000];
	U32 imageNbr=0, idx=0, status;
	Minix_Start(&table,&superblock);
	imageNbr = Minix_LoadImages(&(table.Table),&superblock,inodeBuffer);
	while(1){
		for(idx=0;idx<imageNbr;++idx)
		{
			status = Minix_ReadFileDataZone(&(table.Table),&(inodeBuffer[idx]),image_ptr);
			DisplayBMPImage(image_ptr);
			Timer_delay(5000);
		}
		
	}
}

