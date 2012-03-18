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
#define MASK   0xFF
void DisplayBMPImage(void * image_ptr){
	int line=0 , col=0;
	BITMAPINFO* bitmap = (BITMAPINFO*)(image_ptr);
	RGB * pixelPtr = bitmap->color; //para poder iterar pelos pixeis da imagem.
	RGBPixel pixel;
	for(line= bitmap->info.height-1 ; line >= 0 ; --line )
	{
		for(col=0;col < bitmap->info.width; ++col,pixelPtr++)
		{
			pixel.B = (pixelPtr->blue  >>3 ) & MASK;
			pixel.G = (pixelPtr->green >>2 ) & MASK;
			pixel.R = (pixelPtr->red   >>3 ) & MASK;
			DisplayPixel( pixel, line,	col );
		}
	}
}
void DisplayColor(RGBPixel color){
      int line,col;
		for (line = 0;  line < 600; ++line) {
			for (col = 0; col < 800; ++col) {
				DisplayPixel(color, line, col);
         }
		}
}
