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
#								ASM
#=======================================================================
#Shell Path
SHELL			= /bin/sh
#Environmental Paths
#=======================================================================
PROJECT_PATH	= ./..
#=======================================================================
include $(PROJECT_PATH)/common.mk
#=======================================================================
#Phony Target
.PHONY 					= clean
.PHONY					= clear
.PHONY					= ASM.o
#=======================================================================

lscboot.bin: lscboot.o start16.o start64.o
	$(LD) $(LDSCRIPT) bootrec.ld $(OUTPUT) $@ $<
	dd if=$@ of=$(DSK_HOME)/lsc-hd63-flat.img bs=512 seek=43344 count=1 conv=notrunc
	$(CP)	$@ $(LIB)
	
lscboot.o: lscboot.s
	$(AS) $(OUTPUT) $@ $<
	
start16.o: start16.s
	$(AS) --64 -o $@ $<
	$(CP)	$@ $(LIB)

start64.o: start64.s
	$(AS) --64 -o $@ $<
	$(CP)	$@ $(LIB)
	

clean:
	@echo "----------------------------------------------------------------------"
	$(RM)	*.bin *.o 
	$(RM) $(LIB)/start16.o $(LIB)/start64.o $(LIB)/lscboot.bin
	
	
clear:
	clear
