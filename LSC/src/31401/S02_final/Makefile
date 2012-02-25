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
PROJECT_PATH	= .
#=======================================================================
include $(PROJECT_PATH)/common.mk
MAKEPATHS				= asm dev
#=======================================================================

Main.o: Main.c
	@echo "============================"
	@echo "Compiling the Device Library"
	@echo "============================"	
	@$(foreach MPATH, $(MAKEPATHS), $(MAKE) -C $(MPATH);)
	$(CC) $(EXTRAOPTIONS) $(COPTIONS)$(COMPILE_ONLY) $(OUTPUT) $@ $<
	$(CP)	$@ $(LIB)
	$(MAKE) -C $(LIB)
	
clean:
	@$(foreach MPATH, $(MAKEPATHS), $(MAKE) -C $(MPATH) clean;)
	$(RM) Main.o 
	$(RM) $(LIB)/Main.o
	$(RM) /mnt/lsc/hd0p2/*.sys
	
