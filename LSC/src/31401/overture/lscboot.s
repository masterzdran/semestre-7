.equ START_ADDR, 0x7C00 		# .equ defines a textual substitution
.text 							   # code section starts here

.section .rodata				# program constants (no real protection)

msgb: .asciz "Starting LSC..."

msge:

lsc:

.data
.long 2010 						# program variables (probably not needed)
.code16 						      # this is real mode (16 bit) code
cli 							      # no interrupts while initializing
# ... init ... 					# initialization code...
	ljmp $0, $norm_cs
norm_cs:
	xorw %ax, %ax
	movw %ax, %ds

sti 							      # interrupts enabled after initializing
# ... prog ... 					# main program body...
	mov  $msgb, %ecx 
	movb $'@' , %al				# The character: '@'
	movb $7   , %bl				# Light Gray on Black

/*
#Working
putchar:
   movb  (%ecx), %dl  
   testb %dl, 0xFF
   jz stop
   mov %dl, %al
   movb $0x0E, %ah
   int $0x10
   inc %ecx
   jmp putchar
*/


/*	
   xorb %bh  , %bh           	# Using page 0
	movb $0x0E, %ah				# Identifying the service
	int $0x10						# Invoking the BIOS service
	inc  %cx
	movb %cl,	%al
	test %al, %al
	jz putchar
*/


# ... term ... 					# end of execution...
stop:
	hlt
	jmp stop

.end
   
