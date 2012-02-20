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
#include "minix_fs.h"
#include "timer.h"
#include "BMP.h"
#include "Types.h"

char * const image_ptr = (char*) 0x600000;
iNODE * const images = (iNODE *) 0x400000;
//static iNODE * images;
#define IMAGES_HOME  "image"
static PARTITION part;
static int total;
static int current = 0;

void client_startup()
{	
	int i,j; // iterator variables
	openPartition(&part, 1);
	total = getDirectoryContentLength(&part, IMAGES_HOME);
	getDirectoryContent(&part, IMAGES_HOME, images);	
}

void client_run()
{
   while(1){
      readFile(&part, &images[current], (void*) image_ptr);
      DisplayBMPImage(image_ptr);
      if(++current >= total) current = 0;
      Timer_delay(5000);
   }
}

void lsc_main() {
	client_startup();
	client_run();
}

