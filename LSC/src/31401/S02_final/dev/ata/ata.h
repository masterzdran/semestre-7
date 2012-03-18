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

//READ
#define DATA_PORT       			0x1F0
#define ERROR_REGISTER  			0x1F1
#define SECTOR_COUNT    			0x1F2
#define SECTOR_NUMBER   			0x1F3
#define CYLINDER_LOW    			0x1F4
#define CYLINDER_HIGH   			0x1F5
#define HEAD_PORT       			0x1F6
#define STATUS_REGISTER     		0x1F7
#define ALTERNATIVE_STATUS_REGISTER 0x03F6
//WRITE
#define FEATURES_REGISTER 			0x01F1
#define COMMAND_REGISTER			0x01F7
#define DEVICE_CONTROL_REGISTER 	0x03F6

/**
 * BSY	: Operation in course
 * RDY	: Device not ready
 * DF	: Device fault
 * DRQ	: Data transfer may start
 * ERR	: Error in previous command
 * nIEN : Interrupt Enable (negated)
 * SRST : Reset master and slave devices
 * We will poll the device to detect status changes, so nIEN should be set to 1 at program start.
*/
#define BSY_BIT		1<<7
#define RDY_BIT		1<<6
#define DF_BIT		1<<5
#define DRQ_BIT		1<<3
#define SRST_BIT	1<<2
#define nIEN_BIT	1<<1
#define ERR_BIT		1<<0

#define PIO_MODE		0
#define BYTE_SHIFT      8
#define WORD_SHIFT      16
#define WORD_AND_HALF_SHIFT 24
#define NIBBLE_SHIFT    4
#define READ_SECTORS    0x20
#define MASTER          0xE0
#define SLAVE           0xF0
#define TRF_WORDS       256
#define NIBBLE_MASK		0xF
#define BYTE_MASK		0xFF

U32 ATA_read(U32 LBA, U16* buffer, U16 numberOfSectors);

#endif
