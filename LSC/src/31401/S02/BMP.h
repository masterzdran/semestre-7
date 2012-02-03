#ifndef __BMP_H__
#define __BMP_H__

#include "Types.h"


static int color_debug = 0xffffff; /* white */

typedef struct{
    U8	signature[2]; /* should contain 'MB' */
    U32	fileSize;
    U32	reserved;
    U32	offset;
} __attribute__((packed)) BMP_HEADER;

typedef struct{
    U32 headerSize;
    U32 width;
    U32 height;
    U16 planeCount;
    U16 bitDepth;
    U32 compression;
    U32 compressedImageSize;
    U32 horizontalResolution;
    U32 verticalResolution;
    U32 numColors;
 U32 importantColors;
} BMP_IMAGE_INFO;

typedef struct{
    U8 blue;
    U8 green;
    U8 red;
} __attribute__((packed)) BMP_RGB;

typedef struct {
	BMP_HEADER header;
	BMP_IMAGE_INFO info;
	BMP_RGB colors[1];
} __attribute__((packed)) BMP_24BPP_FILE; 

int DisplayBMPImage(void * image_ptr);

#endif
