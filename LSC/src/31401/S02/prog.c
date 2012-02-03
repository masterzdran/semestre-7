#include "counter.h"
//#include "minixUtils.h"
#include "disk.h"
#include <stdio.h>

typedef struct
{
	unsigned R : 5;
	unsigned G : 6;
	unsigned B : 5;
} __attribute__((packed)) RGBPixel;

typedef RGBPixel PCScreen[600][800];
PCScreen * const screen = (PCScreen*)0x800000;
#define SCREEN (*screen)

extern unsigned char bootdrv;

void writePixel(int color, int line, int column)
{
	RGBPixel aux = { color & 0xff, ((color >>8) & 0xff)<<1, (color >>16) & 0xff };
	SCREEN[line][column] = aux;
}

void demo()
{	
	unsigned line, col;
	int color = (0xff<<16)+(0<<8)+0; // R = 255, G = 0, B = 0
	
	while(1<2)
	{
		// # SHOW RED
		for (line = 0;  line < 600; ++line) {
			for (col = 0; col < 800; ++col) {
				writePixel(color, line, col);
			}
		}
		
		delay(3000); // wait 3 seconds and change color...
		
		// # SHOW GREEN
		color = (0x00<<16)+(0xff<<8)+0; // R = 0, G = 255, B = 0
		
		for (line = 0;  line < 600; ++line) {
			for (col = 0; col < 800; ++col) {
				writePixel(color, line, col);
			}
		}
		
		delay(3000); // wait 3 seconds and change color...
		
		// # SHOW BLUE
		color = (0x00<<16)+(0x00<<8)+0xff; // R = 0, G = 0, B = 255
		
		for (line = 0;  line < 600; ++line) {
			for (col = 0; col < 800; ++col) {
				writePixel(color, line, col);
			}
		}
		
		delay(3000); // wait 3 seconds and change color...
		
		// set RED...
		color = (0xff<<16)+(0<<8)+0; // R = 255, G = 0, B = 0
	}		
}

void lsc_main() {
	startCounter();
	//demo();
	
	/*
	while(1<2)
	{
		//read BMP 
		
		//truncate bit depth to 16 bit ( should truncate size too? )
		
		//print BMP ( lets try to do this fast, shall we? )
		
		//delay ( should use interrupts to save energy... )
		
	}
	*/
	
}
// qemu /mnt/hgfs/Xubuntu/LSC/disks/lsc-hd63-flat.img
