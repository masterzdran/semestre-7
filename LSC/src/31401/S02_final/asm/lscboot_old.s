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
	movw $START_ADDR, %sp		#init Stack Pointer
	
#------------------------------------------------------------------------------
#                                 Init
#------------------------------------------------------------------------------
	sti                      	# interrupts enabled after initializing

	movb %dl, disk_id			#save active disk id
								#the value present in DL when BIOS jumped to 0x7C00 is driveid


#------------------------------------------------------------------------------
#                                 Main
#------------------------------------------------------------------------------
#====================
# SuperBlock
#====================
ReadSuperBlock:						
	movw $dap, %si				#read data to dap
	movb disk_id, %dl			#identify disk to read
	movb $0x42, %ah				#identify service to be called
	int $0x13					#invoke BIOS service
		
		###break 0x7c21
	# First Zone
	movw $dap, %si	
	movw 4(%si), %si			#move content of dap destination offset of memory buffer to si
								#0x7E00 is root dir (i-nodes area)
	movw 24(%si), %si			#go to izone_0
	sal $1, %si
	
	movw first_sector, %di
	
	addw %di, %si				#add izone0_adress to base_address
	movw %si, dap+8				#si points to destination offset of memory buffer
			
	movw $dap, %si				#reading root directory entries
	movb disk_id, %dl
	movb $0x42, %ah
	int $0x13
		###break 0x7c41
		
#------------------------------------------------------------------------------
#                                 Searching Files
#------------------------------------------------------------------------------
FileSearch:
	movw $0x7E42, %si				#where to start search
									#i-node from 0x7E00, size 64 bytes + 2 bytes to begin of file name
	movw $filename, %di				#name of file to search in %di
	movw filename_length, %cx		#size of file lsc.sys + \0 = 8
	
WhileFileSearch:
	
	cmp $0, -2(%si)					#if next byte after i-node is 0 file don't exist -> exit
	jz FileNotFound
	
	cld								#clear direction flag
	repe cmpsb		
	cmp $0, %cx		
	jz FileFound
	#reset search
	movw $filename, %di				#name of file to search in %di
	addw $0x20 , %si				#move to next file name
	addw %cx, %si					#si to point to begin of next file name, not middle
	movw filename_length, %cx		#using the remaining value of cx and filename length
	subw %cx, %si
	jmp WhileFileSearch
	
FileFound:
	subw filename_length, %si		#si to point to begin of file name
	movb -2(%si), %cl				#cl have inode index
	jmp GetNode
FileNotFound:
	jmp stop

#------------------------------------------------------------------------------
#                                 Nodes
#------------------------------------------------------------------------------
GetNode:
	movw $0xA964, dap+8	      		#LBA address of first sector to read

CallRead:                   
	movb disk_id, %dl
	movw $dap, %si      
	movb $0x42, %ah     			#Identifying the service 
	int $0x13           			#Invoking the BIOS service
	movw $0x7E00, %bx				#bx with start address of first node
		###break 0x7c7e
WhileTrue:
	cmp $1,%cl						#is this the correct inode index?					
	je GetInfo
	addw $64, %bx					#go to next node
	decb %cl
	jmp WhileTrue

GetInfo:
	movw 8(%bx),%cx     			#get i_size: file size    		
	movw %cx,filesize				#save filesize
	addw $20,%bx					#bx points to start of izone
	movb $1, %cl

    movw $0x0, dap+4				#prepare DAP to start to copy file to
	movw $0x1000, dap+6	 			#memory zone starting on 0x1000
#------------------------------------------------------------------------------
#                                 Zones
#------------------------------------------------------------------------------
		###break 0x7ca2

ReadZone: 
	addw $4,%bx				#get block number of file
FixedAddress: 	  
	movw (%bx),%ax        
	sal $1,%ax
	addw $0xA950,%ax      
		###break 0x7ca8
	
	movw %ax, dap+8	      

#====================
# Load (lsc.sys)
#====================
	movb disk_id, %dl
	movw $dap, %si      
	movb $0x42, %ah     # Identifying the service 
	int $0x13           # Invoking the BIOS service
	
# calculate next memory address to load data... 
# !watchout! for end of segment @ 65KB -> resolved in StartSegment
	movw dap+4, %dx
	addw $0x400, %dx 	# if(offset > 64KB) { increment segment... } 
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
	filename: 			.asciz "lsc-boot.sys"
	filename_length:	.word 13
	first_sector: 		.word 0xA950		#first sector...
      
      
.data                    			
	filesize: .int  0
	dap:	.byte 	16  			# length of dap
			.byte 	0				# unused (default 0)
			.byte 	2				# number of sectors to read, up to 127
			.byte 	0				# unused (default 0)
			.word 	0x7E00			# destination offset of memory buffer
			.word 	0				# destination segment of memory buffer
			.quad 	0xA964			# LBA address of the first sector to read
			
	disk_id: .word 0
	
.end
