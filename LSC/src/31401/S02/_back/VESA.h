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
#ifndef __VESA_H__
#define __VESA_H__
#include "Types.h"
typedef struct
{
	unsigned R : 5;
	unsigned G : 6;
	unsigned B : 5;
} __attribute__((packed)) RGBPixel,*pRGBPixel;

#define MAX_LINE 600
#define MAX_COL 800

void DisplayPixel(U32 color, U32 line, U32 column);

#endif
