#include "timer.h"
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

void writePixel(unsigned int color, unsigned int line, unsigned int column)
{
	RGBPixel aux = { color & 0xff, ((color >>8) & 0xff)<<1, (color >>16) & 0xff };
	SCREEN[line][column] = aux;
}
#define RED          (0xff<<16)+(0<<8)+0
#define GREEN        (0x00<<16)+(0xff<<8)+0
#define BLUE         (0x00<<16)+(0x00<<8)+0xff

static void show(unsigned int color){
      int line,col;
		for (line = 0;  line < 600; ++line) {
			for (col = 0; col < 800; ++col) {
				writePixel(color, line, col);
			}
		}
}
void lsc_main() {
	Timer_start();
	while(1)
	{
		// # SHOW RED
      show(RED);
		Timer_delay(3000);
		
		// # SHOW GREEN
      show(GREEN);
      Timer_delay(3000);
		
		// # SHOW BLUE
      show(BLUE);
		Timer_delay(3000); // wait 3 seconds and change color...
	}
	
}
