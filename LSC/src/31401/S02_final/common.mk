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
SOURCE				= $(PROJECT_PATH)
TARGET				= $(PROJECT_PATH)/dst
#=======================================================================
LIB				= $(SOURCE)/lib
INCLUDE			= $(SOURCE)/include
SEARCHINCLUDE	= -I$(INCLUDE) 
SEARCHLIB		= -L$(LIB)/
VPATH 			= $(LIB)
#DSK_HOME			= /home/masterzdran/WorkingArea/Isel/semester-7/LSC/dsk
DSK_HOME			= /mnt/hgfs/D/ISEL/Semestre7/LSC/dsk
#Executables
CC 				= gcc
LD 				= ld
AS					= as
AR					= ar rcs
CP					= cp
RM					= rm
OPENOCD			= openocd
#Commom Options
OUTPUT 				= -nostdlib  -o
LDSCRIPT				= -T  
DEBUGSTABS			= --gstabs
DEBUGSYMBOLS		= -g
COPTIONS 			= -m64 -ffreestanding -mno-red-zone  $(SEARCHINCLUDE)
LDFLAGS 				= $(SEARCHLIB) 
EXTRAOPTIONS		= -fno-stack-protector
COMPILE_ONLY 		= -c 






