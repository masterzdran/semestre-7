.intel_syntax noprefix

.equ _DAP_LEN_, 						0xF
.equ _ZERO_,								0x0
.equ _NUMBER_OF_SECTORS_		0x2
.equ _DESTINATION_OFFSET_
.equ _DESTINATION_SEGMENT_
.equ _LBA_ADDRESS_

.equ START_ADDR, 0x7C00 		# .equ defines a textual substitution
.data
dap: 
		.byte		_DAP_len_
		.byte 	_ZERO_
		.byte		_NUMBER_OF_SECTORS_
		.byte 	_ZERO_
		.word 	_DESTINATION_OFFSET_
		.word		_DESTINATION_SEGMENT_
		.dword	_LBA_ADDRESS_FIRST_SECTOR_
		.dword	

.text 											# code section starts here
.code16 										# this is real mode (16 bit) code
cli 												# no interrupts while initializing
# ... init ... 							# initialization code...
	ljmp $0, $norm_cs
norm_cs:
	xorw %ax, %ax
	movw %ax, %ds
	


