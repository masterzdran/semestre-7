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
#define RED       0xFF0000
#define GREEN     0x00FF00
#define BLUE      0x0000FF
#define ORANGE    0xAA0000
#define BLACK     0x00AA00
#define WHITE     0x0000AA
#define BROWN     0x550000
#define YELLOW    0x005500

#define COLOR_SIZE_ARRAY 8
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
    unsigned int colors[]= {BLACK, RED, GREEN, BLUE,BROWN,ORANGE, YELLOW,   WHITE};

   unsigned char size = 0; 
	while(1)
	{
      for (size = 0 ; size < COLOR_SIZE_ARRAY ; ++size){
         show(colors[size]);
         Timer_delay(3000);       
      }
	}
	
}
