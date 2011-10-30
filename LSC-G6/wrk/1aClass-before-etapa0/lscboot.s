 .equ START_ADDR, 0x7C00  	# .equ defines a textual substitution 
 .text                    	# code section starts here 
 .code16                 	# this is real mode (16 bit) code 
 cli                      	# no interrupts while initializing 
 # ... init ...           	# initialization code... 
	ljmp $0, $norm_cs 
norm_cs: 
	xorw %ax, %ax 
	movw %ax, %ds 
	movw %ax, %ss
	movw %ax, %es
	
	movw $0x7C00, %sp
 sti                      	# interrupts enabled after initializing 
 
 # ... prog ...           	# main program body...
	movb  $'@', %al    		# The character: ‘@’ 
	movb  $7  , %bl    		# Light gray on black 
	xorb  %bh , %bh    		# Using page 0 
 
	movb $0x0E, %ah    		# Identifying the service 
	int  $0x10         		# Invoking the BIOS service 
	
 # ... term ...           # end of execution... 
stop: 
	hlt 
	jmp stop 
 
		.section .rodata         # program constants (no real protection) 
msgb: 	.asciz "Starting LSC..." 
msge: 
 
		.data                    # program variables (probably not needed) 
lsc:  	.long 2010 
 
		.end
		
