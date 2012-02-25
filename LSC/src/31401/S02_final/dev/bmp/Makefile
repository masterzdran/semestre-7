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
#								BMP
#=======================================================================
#Shell Path
SHELL			= /bin/sh
#Environmental Paths
#=======================================================================
PROJECT_PATH	= ./../..
#=======================================================================
include $(PROJECT_PATH)/common.mk
#=======================================================================
#Phony Target
.PHONY 					= clean
.PHONY					= clear
#=======================================================================
BMP.o: BMP.c BMP.h
	$(CC) $(COPTIONS) $(COMPILE_ONLY) $(OUTPUT) $@ $<  
	$(CP)	$@ $(LIB) 
	$(CP) BMP.h $(INCLUDE)

clean:
	@echo "----------------------------------------------------------------------"
	$(RM) $(LIB)/BMP.o $(INCLUDE)/BMP.h
	$(RM)	*.o 
	
	
clear:
	clear
