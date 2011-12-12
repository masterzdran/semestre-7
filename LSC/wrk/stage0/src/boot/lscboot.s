.equ START_ADDR, 0x7C00 		# .equ defines a textual substitution
.text 							# code section starts here
.code16 						# this is real mode (16 bit) code
cli 							# no interrupts while initializing
# ... init ... 					# initialization code...
	ljmp $0, $norm_cs
norm_cs:
	xorw %ax, %ax
	movw %ax, %ds

sti 							# interrupts enabled after initializing
# ... prog ... 					# main program body...
	lea  msgb, %cx 
	movb $'@' , %al					# The character: '@'
	movb $7   , %bl					# Light Gray on Black

putchar:
	xorb %bh  , %bh           		# Using page 0
	movb $0x0E, %ah					# Identifying the service
	int $0x10						# Invoking the BIOS service
	inc  %cx
	movb %cl,	%al
	test %al, %al
	jz putchar



# ... term ... 					# end of execution...
stop:
	hlt
	jmp stop

.section .rodata				# program constants (no real protection)

msgb: .asciz "Starting LSC..."

msge:

lsc:

.data
.long 2010 						# program variables (probably not needed)
.end
