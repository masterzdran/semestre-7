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
#include "VESA.h"
#define MAX_LINE 600
#define MAX_COL  800
typedef RGBPixel PCScreen[MAX_LINE][MAX_COL];
PCScreen * const screen = (PCScreen*)0x800000; 
#define SCREEN (*screen)

void DisplayPixel(RGBPixel pixel, U32 line, U32 column)
{
	SCREEN[line][column] = pixel;
}
