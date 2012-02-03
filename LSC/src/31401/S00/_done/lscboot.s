.equ START_ADDR, 0x7C00  			# .equ defines a textual substitution
.text                    			# code section starts here
.code16                  			# this is real mode (16 bit) code		  
		

#------------------------------------------------------------------------------
#                                 Cleaning
#------------------------------------------------------------------------------
  cli                      	# no interrupts while initializing
	ljmp $0, $norm_cs
	
norm_cs:
	xorw %ax, %ax
	movw %ax, %ds
	movw %ax, %ss
	movw %ax, %es
	movw  $0x7C00, %sp
	
#------------------------------------------------------------------------------
#                                 Init
#------------------------------------------------------------------------------
	sti                      	# interrupts enabled after initializing

	movb %dl, disk_id         #save active disk id


#------------------------------------------------------------------------------
#                                 Main
#------------------------------------------------------------------------------
#====================
# SuperBlock
#====================
ReadSuperBlock:						
  movw $dap, %si		
	movb disk_id, %dl
	movb $0x42, %ah
	int $0x13	
		
	# First Zone
	movw $dap, %si	
	movw 4(%si), %si 
	movw 24(%si), %si	
	sal $1, %si
	
	movw first_sector, %di
	
	addw %di, %si				  # add izone0_adress to base_address
	movw %si, dap+8		
			
	movw $dap, %si				# reading root directory entries
	movb disk_id, %dl
	movb $0x42, %ah
	int $0x13
	
#------------------------------------------------------------------------------
#                                 Searching Files
#------------------------------------------------------------------------------
FileSearch:
	movw $0x7E42, %si
	movw $filename, %di
	movw filename_length, %cx
	
WhileFileSearch:
	cmp $0, -2(%si)
	jz FileNotFound
	
	cld
	repe cmpsb		
	cmp $0, %cx		
	jz FileFound
	addw $0x20, %si
	jmp WhileFileSearch
	
FileFound:
	subw filename_length, %si
	movb -2(%si), %cl		
	jmp GetNode
FileNotFound:
	jmp stop

#------------------------------------------------------------------------------
#                                 Nodes
#------------------------------------------------------------------------------
GetNode:
	movw $0xA964, dap+8	      

CallRead:                   
	movb disk_id, %dl
	movw $dap, %si      
	movb $0x42, %ah     # Identifying the service 
	int $0x13           # Invoking the BIOS service
	movw $0x7E00, %bx

WhileTrue:
	cmp $1,%cl
	je GetInfo
	addw $64, %bx
	decb %cl
	jmp WhileTrue

GetInfo:
	movw 8(%bx),%cx         
	movw %cx,filesize
	addw $20,%bx
	movb $1, %cl

  movw $0x0, dap+4
	movw $0x1000, dap+6	 
#------------------------------------------------------------------------------
#                                 Zones
#------------------------------------------------------------------------------
ReadZone: 
	addw $4,%bx
FixedAddress: 	  
	movw (%bx),%ax        
	sal $1,%ax
	addw $0xA950,%ax      # bochs -> b 0x7ca8
	
	movw %ax, dap+8	      

#====================
# Load (lsc.sys)
#====================
	movb disk_id, %dl
	movw $dap, %si      
	movb $0x42, %ah     # Identifying the service 
	int $0x13           # Invoking the BIOS service
	
## calculate next memory address to load data... 
## !watchout! for end of segment @ 65KB -> resolved in StartSegment
	movw dap+4, %dx
	addw $0x400, %dx # if(offset > 64KB) { increment segment... } 
	jc StartSegment

EndLoad:
	movw %dx, dap+4
	
## cmp file size with read length	
	xorw %dx, %dx
	movb %cl, %dl
	sal $10,%dx
	cmp filesize, %dx
	jg Terminate             # if( filesize < data read){ exit routine }

## increment read count...
	incb %cl                # bochs -> b 0x7cd0
	cmpb $8,%cl
	jne ReadZone 
	
## first indirection trick goes here...
	addw $4,%bx
	push %bx
	
	movw (%bx), %ax
	sal $1,%ax
	addw $0xA950,%ax
	
	movw %ax, dap+8	        ## set new LBA address in DAP
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
	
	jmp FixedAddress
	
StartSegment:
	# %dx == 0, this means i've got to increment the segment that i'm using...
	movw dap+6,%dx
	addw $0x1000,%dx
	movw %dx, dap+6
	xor %dx, %dx
 	jmp EndLoad
	
Terminate:
	pop %bx
	ljmp $0x1000, $0	# long jump (segment, offset)
stop:
	hlt
	jmp stop

.section .rodata         			# program constants (no real protection)
	filename: 			  .asciz "lsc-3.sys"
	filename_length:	.word 8
	first_sector: 		.word 0xA950		#first sector...
      
      
.data                    			
	filesize: .int  0
	dap:	.byte 	16  			# length of dap
			.byte 	0				# unused
			.byte 	2				# number of sectors to read
			.byte 	0				# unused
			.word 	0x7E00			# destination offset of memory buffer
			.word 	0				# destination segment of memory buffer
			.quad 	0xA964			# LBA address of the first sector to read
			
	disk_id: .word 0
	
.end
