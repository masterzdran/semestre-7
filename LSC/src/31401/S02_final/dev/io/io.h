/*
#=======================================================================
# LSC   - Laboratorio de Sistemas Computacionais
#-----------------------------------------------------------------------
# Turma:	LI51N
# Semestre:	Inverno 2011/2012
# Data:		Fevereiro/2011
#-----------------------------------------------------------------------
# Nome: 	   Nuno Cancelo
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
#ifndef __IO_H__
#define __IO_H__
#include "Types.h"
// Source : http://lxr.linux.no/linux+v2.6.32.34/arch/x86/include/asm/io_32.h#L186
// Source : http://lxr.linux.no/#linux+v2.6.32.34/arch/x86/boot/boot.h
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
static inline void rep_insw(int port, void *addr, unsigned long count)
{									
	asm volatile("rep; insw" : "+D"(addr), "+c"(count) : "d"(port));
}
#endif
