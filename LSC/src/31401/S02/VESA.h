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
