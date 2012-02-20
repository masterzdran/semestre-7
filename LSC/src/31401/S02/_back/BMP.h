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
#ifndef __BMP_H__
#define __BMP_H__

//source #1: http://www.ue.eti.pg.gda.pl/fpgalab/zadania.spartan3/zad_vga_struktura_pliku_bmp_en.html 
//source #2: http://paulbourke.net/dataformats/bmp/

#include "Types.h"

typedef struct{
    U16	type;
    U32	size;
    U16	reserved1;
    U16	reserved2;
    U32	offset;
} __attribute__((packed)) BMP_HEADER;  //

typedef struct{
    U32 size;
    S32 width;
    S32 height;
    U16 planes;
    U16 bits;
    U32 compression;
    U32 imagesize;
    S32 xresolution;
    S32 yresolution;
    U32 ncolours;
    U32 importantcolours;
} BMP_INFOHEADER;

typedef enum {
   NO_COMPRESSION = 0,
   EIGHT_BIT_RUN  = 1,
   FOUR_BIT_RUN   = 2,
   RGB_W_MASK     = 3
}BMP_COMPRESSION;


typedef struct{
    U8 blue;
    U8 green;
    U8 red;
} __attribute__((packed)) BMP_RGB_PIXEL;

typedef struct{
    U8  blue;    
    U8  green;   
    U8  red;     
    U8  reserved;
    }  __attribute__((packed)) BMP_RGB_QUAD;
   
typedef struct {
	BMP_HEADER     header;
	BMP_INFOHEADER info;
	BMP_RGB_PIXEL colors[1];
} __attribute__((packed)) BMP_24BPP_FILE; 

void DisplayBMPImage(void * image_ptr);

#endif
