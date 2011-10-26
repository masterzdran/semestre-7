.equ START_ADDR,	0x7C00 		# .equ defines a textual substitution
.equ MEM_ADDR,		0x1000		# MEM Address

.data
driveid:
    .byte  0

dap:
    .byte  16
    .byte  0
    .byte  2
    .byte  0
    .word  0
    .word  MEM_BUFFER
    .quad 45150					#903 zonas = 1806 sectors + LBA Start Offset 43344 = 45150 - first sector to be read
    
.textcode section starts here
.code16 						# this is real mode (16 bit) code
cli 							# no interrupts while initializing
# ... init ... 					# initialization code...
	ljmp $0, $norm_cs
norm_cs:
	xorw %ax, %ax
	movw %ax, %ds
	movw %ax, %ss
	movw %ax, %es
	movw $START_ADDR, %sp 		#init Stack
	movb %dl, driveid			#the value present in DL when BIOS jumped into 0x07C00 is driveid


sti 							# interrupts enabled after initializing
# ... prog ... 					# main program body...

    movw  $dap		,%si
    movb  $0x42		,%ah
    movb  driveid	,%dl
    int   $0x13

