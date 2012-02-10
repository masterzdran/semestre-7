.equ START_ADDR, 0x7C00 		# .equ defines a textual substitution
.text 							# code section starts here
.code16 						# this is real mode (16 bit) code
cli 							# no interrupts while initializing
# ... init ... 					# initialization code...
	ljmp $0, $norm_cs
norm_cs:
	xorw %ax, %ax
	movw %ax, %ds
	movw %ax, %ss
	movw %ax, %es
	
sti 							# interrupts enabled after initializing
# ... prog ... 					# main program body...

main:
#	movb $'@', %al				# The character: '@'
#	movb $7   , %bl				# Light Gray on Black
#	xorb %bh  , %bh   			# Using page 0
#	movb $0x0E, %ah				# Identifying the service
#	int $0x10					# Invoking the BIOS service

	movl msgb , %ecx             #pointer para string msgb
	movb (%ecx), %al
	test $0, %al
	jz	 stop
	call putchar
	inc	 %cx
	jmp  main

putchar:
#	movb %cl  , %al				# The character on cl position
	movb $7   , %bl				# Light Gray on Black
	xorb %bh  , %bh      		# Using page 0
	movb $0x0E, %ah				# Identifying the service
	int  $0x10					# Invoking the BIOS service



# ... term ... 					# end of execution...
stop:
	hlt
	jmp stop


	.section .rodata			# program constants (no real protection)

msgb: .asciz "Starting LSC..."
msge:

	.data
lsc:	
	.long 2010 					# program variables (probably not needed)
	.end
