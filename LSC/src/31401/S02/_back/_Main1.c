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
#include "timer.h"
#include "VESA.h"
extern unsigned char bootdrv;

#define RED       0x0000FF
#define GREEN     0x00FF00
#define BLUE      0xFF0000
#define ORANGE    0xA5A5A5
#define BLACK     0x000000
#define WHITE     0xFFFFFF
#define BROWN     0x5A5A5A
#define COLOR_SIZE_ARRAY 4


static void show(U32 color){
      int line,col;
		for (line = 0;  line < 600; ++line) {
			for (col = 0; col < 800; ++col) {
				DisplayPixel(color, line, col);
         }
		}
}

void lsc_main() {
	Timer_start();
   unsigned int colors[]= {BLUE, GREEN,BLACK,WHITE};

   char size = 0; 
	while(1)
	{
      for (size = 0  ; size < COLOR_SIZE_ARRAY ; ++size){
         show(colors[size]);
         Timer_delay(3000);       
      }
	}
	
}
