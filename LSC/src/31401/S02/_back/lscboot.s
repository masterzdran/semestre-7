.equ START_ADDR, 0x7C00  			# .equ defines a textual substitution

.text                    			# code section starts here
.code16                  			# this is real mode (16 bit) code		  
		
	cli                      	# no interrupts while initializing
	ljmp $0, $norm_cs
	
norm_cs:
	xorw %ax, %ax
	movw %ax, %ds
	movw %ax, %ss
	movw %ax, %es
	movw  $0x7C00, %sp
	
##### ... init ...          	# initialization code...	

	sti                      	# interrupts enabled after initializing

	movb %dl, disk_id


##### ... prog ...          	# main program body...			

read_data:						#reading superblock
	movw $dap, %si		
	movb disk_id, %dl
	movb $0x42, %ah
	int $0x13	
		
	## read first_data_zone
	movw $dap, %si	
	movw 4(%si), %si 
	movw 24(%si), %si	
	sal $1, %si
	
	movw first_sector, %di
	
	addw %di, %si				# add izone0_adress to base_address
	movw %si, dap+8		
			
	movw $dap, %si				# reading root directory entries
	movb disk_id, %dl
	movb $0x42, %ah
	int $0x13
	
## FILE SEARCH

FILE_SEARCH:
	movw $0x7E42, %si
	movw $filename, %di
	movw filename_length, %cx
	
file_search_while:
	cmp $0, -2(%si)
	jz file_not_found
	
	cld
	repe cmpsb		
	cmp $0, %cx		
	jz file_found
	addw $0x20, %si
	jmp file_search_while
	
file_found:
	subw filename_length, %si
	movb -2(%si), %cl		
	jmp NODE_FETCH
file_not_found:
	jmp stop

## END FILE SEARCH					
	
	
	
## NODE FETCH
NODE_FETCH:
	movw $0xA964, dap+8	## go back to inodes start addr

invoke_read2: ## read inodes zone
	movb disk_id, %dl
	movw $dap, %si      
	movb $0x42, %ah     # Identifying the service 
	int $0x13           # Invoking the BIOS service
	
	## go to inode number stored in %cx, load 
	movw $0x7E00, %bx

	## iterate to node nr stored in CX
ITT:
	cmp $1,%cl
	je FETCH
	addw $64, %bx
	decb %cl
	jmp ITT

FETCH:
	movw 8(%bx),%cx # size of file
	movw %cx,filesize
	
	addw $20,%bx
	movb $1, %cl

	## prepare DAP...
	movw $0x0, dap+4
	movw $0x1000, dap+6	 
	
## read i node zones while( filesize > nr reads * size of reads ) 
## nr reads {1, 2, ... 10} -> after the seventh read we have to deal with indirections
## size of reads = sector_size * sectors_read (512 Bytes * 2 = 1KB)
	
## if(nr reads==8) { ...carrega a 1a indirecçao... }

READ_ZONES: 
	addw $4,%bx
INDIRECTION_TRICK: 	# after loading first indirection addr to %bx we dont increment it, bcause the first addr is already in the register
	movw (%bx),%ax ## now AX contains the data zone addr
	sal $1,%ax
	addw $0xA950,%ax # bochs -> b 0x7ca8
	
	movw %ax, dap+8	## set new LBA address in DAP
	
## load part of lsc.sys data to memory
	movb disk_id, %dl
	movw $dap, %si      
	movb $0x42, %ah     # Identifying the service 
	int $0x13           # Invoking the BIOS service
	
## calculate next memory address to load data... 
## !watchout! for end of segment @ 65KB -> resolved in START_INC_SEGMENT
	movw dap+4, %dx
	addw $0x400, %dx # if(offset > 64KB) { increment segment... } 
	jc START_INC_SEGMENT
END_INC_SEGMENT:
	movw %dx, dap+4
	
## cmp file size with read length	
	xorw %dx, %dx
	movb %cl, %dl
	sal $10,%dx
	cmp filesize, %dx
	jg PRE_STOP # if( filesize < data read){ exit routine }

## increment read count...
	incb %cl # bochs -> b 0x7cd0
	cmpb $8,%cl
	jne READ_ZONES 
	
## first indirection trick goes here...
	addw $4,%bx
	push %bx
	
	movw (%bx), %ax
	sal $1,%ax
	addw $0xA950,%ax
	
	movw %ax, dap+8	## set new LBA address in DAP
	push dap+4
	push dap+6
	
	movw $8200,%bx
	movw %bx, dap+4
	movw $0,dap+6
	
## load first indirection pointers to memory
	movb disk_id, %dl
	movw $dap, %si      
	movb $0x42, %ah     # Identifying the service 
	int $0x13           # Invoking the BIOS service
	
	pop dap+6
	pop dap+4
	
	jmp INDIRECTION_TRICK
	
START_INC_SEGMENT:
	# %dx == 0, this means i've got to increment the segment that i'm using...
	movw dap+6,%dx
	addw $0x1000,%dx
	movw %dx, dap+6
	xor %dx, %dx
 	jmp END_INC_SEGMENT
	
PRE_STOP:
	pop %bx
	ljmp $0x1000, $0	# long jump (segment, offset)
	
##### ... term ...           	# end of execution...

stop:
	hlt
	jmp stop

.section .rodata         			# program constants (no real protection)
	filename: 			.asciz "lsc.sys"
	filename_length:	.word 8
	first_sector: 		.word 0xA950		#first sector...
      
      
.data                    			# program variables (probably not needed)
	filesize: .int  0
	#segment:.word	1000	
	dap:	.byte 	16  			# length of dap
			.byte 	0				# unused
			.byte 	2				# number of sectors to read
			.byte 	0				# unused
			.word 	0x7E00			# destination offset of memory buffer
			.word 	0				# destination segment of memory buffer
			.quad 	0xA964			# LBA address of the first sector to read
			
	disk_id: .word 0
	
.end
