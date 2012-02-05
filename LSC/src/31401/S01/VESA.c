/*
#=======================================================================
# LSC   - Laboratorio de Sistemas Computacionais
#-----------------------------------------------------------------------
# Turma:	LI51N
# Semestre:	Inverno 2011/2012
# Data:		Fevereiro/2011
#-----------------------------------------------------------------------
# Nome: 	Nuno Cancelo
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
#include "VESA.h"
typedef RGBPixel PCScreen[MAX_LINE][MAX_COL];
PCScreen * const screen = (PCScreen*)0x800000; 
#define SCREEN (*screen)

void DisplayPixel(U32 color, U32 line, U32 column)
{
	RGBPixel aux = { (color >>16) & 0xff, ((color >>8) & 0xff)<<1, color & 0xff };
	SCREEN[line][column] = aux;
}
