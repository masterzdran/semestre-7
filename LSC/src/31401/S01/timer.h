/*
#=======================================================================
# LSC   - Laboratorio de Sistemas Computacionais
#-----------------------------------------------------------------------
# Turma:	LI51N
# Semestre:	Inverno 2011/2012
# Data:		Fevereiro/2011
#-----------------------------------------------------------------------
# Nome: 	Nuno Cancelo
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
#ifndef __TIMER__
#define __TIMER__
//Control Word Format
//Select Counter
#define __SELECT_COUNTER_0__     (0x40)
#define __SELECT_COUNTER_1__     (0x41)
#define __SELECT_COUNTER_2__     (0x42)
#define __CONTROL__    (0x43)
//Read/Write
#define __COUNTER_LATCH_COMMAND__            (0x00 << 4) 
#define __READ_WRITE_LSB_ONLY__              (0x01 << 4)
#define __READ_WRITE_MSB_ONLY__              (0x02 << 4)
#define __READ_WRITE_LSB_FIRST_MSB_AFTER__   (0x03 << 4)
//Mode
#define __MODE_0__         (0x00 << 1)
#define __MODE_1__         (0x01 << 1)
#define __MODE_2__         (0x02 << 1)
#define __MODE_3__         (0x03 << 1)
#define __MODE_4__         (0x04 << 1)
#define __MODE_5__         (0x05 << 1)
//BCD
#define __BINARY_COUNTER_16B__         (0x00 << 0)
#define __BINARY_CODED_DECIMAL__       (0x01 << 0)

#define LOW_BYTE_MASK      (0xFF)
#define ACTION             (0x34)
#define RESET_VALUE        (0x9E0)
void Timer_start();
void Timer_delay(long milis);
#endif
