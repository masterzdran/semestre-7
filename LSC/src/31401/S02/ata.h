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
#ifndef __ATA_H__
#define __ATA_H__

#include "io.h"
#include "Types.h"
#define ON     1
#define OFF    0
#define NULL   0
#define DEVICE_CONTROL_REGISTER  0x3F6
#define nIEN 0x1
#define SRST 0x2
#define DATA_PORT       0x1F0
#define ERROR_REGISTER  0x1F1
#define SECTOR_COUNT    0x1F2
#define SECTOR_NUMBER   0x1F3
#define CYLINDER_LOW    0x1F4
#define CYLINDER_HIGH   0x1F5
#define HEAD_PORT       0x1F6
#define COMMAND_PORT    0x1F7
#define BYTE_SHIFT      8
#define WORD_SHIFT      16
#define WORD_AND_HALF_SHIFT 24
#define NIBBLE_SHIFT    4
#define READ_SECTORS    0x20
#define MASTER          0xE0
#define SLAVE           0xF0
#define TRF_WORDS       256
U32 ATA_read(U16 * destination_buffer, U32 LBA, U16 nr_sectors);

#endif
