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
	movw $0x7c00, %sp
sti 							# interrupts enabled after initializing
# ... prog ... 					# main program body...

main:

	#read from disk to memory
    movw  $dap  , %si		#read data to dap
    movb  $0x42	, %ah		#identify service to be called
    int   $0x13				#invoke BIOS service

	#print data to screen

	##to do : check for errors
	#if get error it's on AH

continue:
	movw $0x7D00, %si
	movb (%si), %al
	cmp $0x8500, %al #compare al with char '0'
	je stop
	
putchar:
	movb 	$7   , %bl				# Light Gray on Black
	xorb 	%bh  , %bh      		# Using page 0
	movb 	$0x0E, %ah				# Identifying the service
	int  	$0x10					# Invoking the BIOS service
	
	incw %si
	jmp continue


# ... term ... 					# end of execution...
stop:
	hlt
	jmp stop


.section .rodata				# program constants (no real protection)

.data							# program variable
dap: .byte 	16        	# length  
     .byte 	0         	# unused (default 0)
     .byte 	2         	# number of sectors to read, up to 127
	 .byte 	0         	# unused (default 0)
	 .short 0x7D00   	# destination offset of memmory buffer
	 .short 0        	# destination segment of memmory buffer
	 .quad 	0xA964    	# LBA address of first sector to read
.end
