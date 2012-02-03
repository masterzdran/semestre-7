#include "BMP.h"
#include "VESA.h"
int DisplayBMPImage(void * image_ptr){
	int line=MAX_LINE;
	int col=0;
	int display = 0;
	BMP_RGB * color = ((BMP_24BPP_FILE*)(image_ptr))->colors;

	for(line=MAX_LINE-1 ; line >= 0; --line )
	{
		for(col=0;col<MAX_COL;col++)
		{
			display = (color->red)>>3 & 0xFF;
			display |= ( (color->green)>>3 & 0xFF) << 8; 
			display |= ( (color->blue)>>3 & 0xFF ) << 16; 
			color++;
			DisplayPixel( display, line,	col );
		}
	}
}

