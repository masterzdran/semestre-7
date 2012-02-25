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
#include "BMP.h"
#include "VESA.h"
#define MASK   0xFF
#define SHIFT        3
#define BYTE_SHIFT   8
#define WORD_SHITT   16
int DisplayBMPImage(void * image_ptr){
	int line , col=0 , display = 0;
   
	RGB * color = ((BITMAPINFO*)(image_ptr))->color;

	for(line=MAX_LINE -1 ; line >= 0 ; --line )
	{
		for(col=0;col < MAX_COL; ++col,color++)
		{
			display  = (  color->red  )>>SHIFT & MASK ;
			display |= ( (color->green)>>SHIFT & MASK ) << BYTE_SHIFT; 
			display |= ( (color->blue )>>SHIFT & MASK ) << WORD_SHITT; 
			DisplayPixel( display, line,	col );
		}
	}
}

