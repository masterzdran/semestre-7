#ifndef __IO_H__
#define __IO_H__
#include "Types.h"
// Source : http://www.cs.fsu.edu/~baker/devices/lxr/http/source/linux/arch/x86/boot/boot.h
static inline void outb(U8 v, U16 port)
{
    asm volatile("outb %0,%1" : : "a" (v), "dN" (port));
}

static inline U8 inb(U16 port)
{
	U8 v;
	asm volatile("inb %1,%0" : "=a" (v) : "dN" (port));
	return v;
}

static inline U16 inw(U16 port)
{
	U16 v;
	asm volatile("inw %1,%0" : "=a" (v) : "dN" (port));
	return v;
}

#endif
