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
#define IMAGES_HOME  "image"
static PARTITION part;
static int total;

void lsc_startup()
{	
   SimpleDir directory;
   char buffer[BUFFER_SIZE];
	readPartition(&part, 1);
	total = getDirectoryContentLength(&part, IMAGES_HOME,&buffer,&directory);
	getDirectoryContent(&part, IMAGES_HOME, images,&directory);	
}

void lsc_run()
{
   int idx = 0;
   //while(1){
   for(idx = 0 ; idx + 1 ;idx = (idx + 1) % total){
      readFile(&part, &images[idx], (void*) image_ptr);
      DisplayBMPImage(image_ptr);
      Timer_delay(5000);
      //current = (current + 1) % total;
   }
}

void lsc_main() {
	lsc_startup();
	lsc_run();
}

