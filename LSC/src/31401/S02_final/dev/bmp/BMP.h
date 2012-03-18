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

#include "Types.h"
#include "VESA.h"
//Source :  http://paulbourke.net/dataformats/bmp/BITMAP.H

typedef struct{
    U8	signature[2];
    U32	fileSize;
    U32	reserved;
    U32	offset;
} __attribute__((packed)) BITMAPFILEHEADER;

typedef struct{
    U32 headerSize;
    U32 width;
    U32 height;
    U16 planeCount;
    U16 bitDepth;
    U32 compression;
    U32 compressedImageSize;
    U32 horizontalResolution;
    U32 verticalResolution;
    U32 numColors;
 U32 importantColors;
} BITMAPINFOHEADER;

typedef struct{
    U8 blue;
    U8 green;
    U8 red;
} __attribute__((packed)) RGB;

typedef struct {
	BITMAPFILEHEADER header;
	BITMAPINFOHEADER info;
	RGB color[1];
} __attribute__((packed)) BITMAPINFO; 

void DisplayBMPImage(void * image_ptr);
void DisplayColor(RGBPixel color);

#endif
