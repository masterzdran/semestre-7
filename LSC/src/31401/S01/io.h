#ifndef __IO__
#define __IO__

typedef unsigned char u8;
typedef unsigned short u16;

// Basic port I/O
// http://lxr.linux.no/linux+v2.6.32.34/arch/x86/boot/boot.h

inline void outb(u8 v, u16 port)
{
    asm volatile("outb %0,%1" : : "a" (v), "dN" (port));
}

inline u8 inb(u16 port)
{
	u8 v;
	asm volatile("inb %1,%0" : "=a" (v) : "dN" (port));
	return v;
}

#endif