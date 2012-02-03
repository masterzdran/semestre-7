#include "VESA.h"

typedef RGBPixel PCScreen[MAX_LINE][MAX_COL];
PCScreen * const screen = (PCScreen*)0x800000; 
#define SCREEN (*screen)

void DisplayPixel(int color, int line, int column)
{
	RGBPixel aux = { (color >>16) & 0xff, ((color >>8) & 0xff)<<1, color & 0xff };
	SCREEN[line][column] = aux;
}
