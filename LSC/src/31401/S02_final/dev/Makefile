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
#								
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
MAKEPATHS				= types io timer ata vesa bmp fs 
#=======================================================================
all: 
	@echo "============================"
	@echo "Compiling the Device Library"
	@echo "============================"	
	@$(foreach MPATH, $(MAKEPATHS), $(MAKE) -C $(MPATH);)

clean:
	@echo "============================"
	@echo "Cleaning the Device Library"
	@echo "============================"	
	@$(foreach MPATH, $(MAKEPATHS), $(MAKE) -C $(MPATH) clean;)
	
clear:
	clear

