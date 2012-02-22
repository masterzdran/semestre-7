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
#define MASK 0xFF
int DisplayBMPImage(void * image_ptr){
	int line , col=0 , display = 0;
	RGB * color = ((BITMAPINFO*)(image_ptr))->color;

	for(line=MAX_LINE -1 ; line >= 0 ; --line )
	{
		for(col=0;col < MAX_COL; ++col,color++)
		{
			display  = (  color->red  )>>3 & MASK ;
			display |= ( (color->green)>>3 & MASK ) << 8; 
			display |= ( (color->blue )>>3 & MASK ) << 16; 
			DisplayPixel( display, line,	col );
		}
	}
}

