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
#=======================================================================
#=======================================================================
#								lib
#=======================================================================
#Shell Path
SHELL			= /bin/sh
#Environmental Paths
#=======================================================================
PROJECT_PATH	= ./..
LD_SCRIPT64		= $(PROJECT_PATH)/asm/lsc64.ld
#=======================================================================
include $(PROJECT_PATH)/common.mk
#=======================================================================

install: lsc-boot.sys lscboot.bin
	$(RM) -f /mnt/lsc/hd0p2/*.sys
	$(CP) lsc-boot.sys /mnt/lsc/hd0p2/
	ln /mnt/lsc/hd0p2/lsc-boot.sys /mnt/lsc/hd0p2/lsc.sys
	
lsc-boot.sys: start16.o start64.o TIMER.o ATA.o MINIX_FS.o BMP.o  VESA.o  Main.o 
	$(LD) $(LDSCRIPT) $(LD_SCRIPT64) $(OUTPUT) $@ $^

