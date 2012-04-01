.equ START_ADDR,	0x7C00 			# .equ defines a textual substitution
.equ START_BUFFER,	0x7E00			
.equ MEM_BUFFER, 	0x1000		  	# mem buffer position

.equ DISK_ROOT_ADDR,				0x0
.equ PARTITION_TABLE_START_ADDR, 	446
.equ PARTITION_ACTIVE,				0x80
.equ PARTITION_INFO_SIZE,			16

.equ BLOCK_LBA_SIZE,				2
.equ SECTORS_PER_BLOCK,				2
.equ I_NODE_SIZE,					64
.equ I_NODE_MAX_DIRECT,				7

.equ ONE_SECTOR,					0x1
.equ INODE_BUFFER,					16
.equ MINIX_PARTITION,				1
.equ SIZE_OF_BUFFER,				1024


    
.text							# code section starts here
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
	
	movb %dl, diskid			#the value present in DL when BIOS jumped into 0x07C00 is the diskid

sti 							# interrupts enabled after initializing
# ... prog ... 					# main program body...


	###b 0x7c16

	#disk sectors to read
	movw $DISK_ROOT_ADDR, dap_first_sector	#to read the first block
	
	#read data from disk to dap
	movw $dap, %si							#read data to dap
	movb diskid, %dl						#identify disk to read
	movb $0x42, %ah							#identify service to be called
	int  $0x13								#invoke BIOS service	

	###b 0x7c27
	movw $START_BUFFER, %bx
	addw $PARTITION_TABLE_START_ADDR, %bx
	#bx points to begin of partition table
	
find_active_partition:
	cmpb %dl, (%bx)
	je	 active_partition_found
	addw $PARTITION_INFO_SIZE, %bx
	jmp  find_active_partition
	
active_partition_found:
	###b 0x7c37
	movw  8(%bx), %bx
	movw %bx, LBA_Start

	#LBA_Start has the LBA address of the start of the partition
	
#Read SuperBlock
	
	#movw LBA_Start, %bx
	addw $BLOCK_LBA_SIZE, %bx		#LBA start plus 2 sectors for boot block
	movw %bx, dap_first_sector		#put first sector to be read in DAP

	#read SuperBlock from disk to dap
	movw $dap, %si
	movb diskid, %dl
	movb $0x42, %ah
	int  $0x13	

	#with SuperBlock we need to find location for iNodes
	#and also read first data zone (root directory)
	###b 0x7c50
	
	#get I-Node zone start LBA address
	movw LBA_Start, %ax
	addw $BLOCK_LBA_SIZE, %ax		#plus 2 sectors for BootBlock
	addw $BLOCK_LBA_SIZE, %ax		#plus 2 sectors for SuperBlock
	#add  s_imap_blocks * 2
	movw $START_BUFFER, %bx
	addw $4, %bx
	movw (%bx), %cx
	shlw $1, %cx			#x2
	addw %cx, %ax
	#add  s_zmap_blocks
	addw $2, %bx
	movw (%bx), %cx
	shlw $1, %cx			#x2
	addw %cx, %ax
	movw %ax, LBA_I_Node_Start

	addw $2, %bx					#number of blocks until first data zone offset
	movw (%bx), %ax
	shlw $1, %ax	#x2
	addw LBA_Start, %ax				#added with start of partition
	movw %ax, LBA_Data_Start		#LBA address of first data block (root directory)

	
	#get I-Node id for file we want
	#movw LBA_Data_Start, %bx
	movw %ax, dap_first_sector
	
	#read root dir from disk to dap
	movw $dap, %si
	movb diskid, %dl
	movb $0x42, %ah
	int  $0x13
	
	###b 0x7c93
	movw $START_BUFFER, %si
	movw $filename, %di				#name of file to search in %di
	movw filename_length, %cx		#size of file lsc-boot.sys + \0 = 13

	add $2, %si						#point to file name
	
WhileFileSearch:
	cmp $0, %si						#if 1st byte of i-node is 0 file don't exist -> exit
	jz FileNotFound
	
	cld								#clear direction flag
	repe cmpsb		
	test %cx, %cx		
	jz FileFound
	#reset search
	movw $filename, %di				#name of file to search in %di
	addw $0x20 , %si				#move to next file name
	addw %cx, %si					#si to point to begin of next file name, not middle
	movw filename_length, %cx		#adding the remaining value of cx and subtracting filename length
	subw %cx, %si
	jmp WhileFileSearch

FileFound:
	subw filename_length, %si		#si to point to begin of file name
	movb -2(%si), %cl				#cl have inode index
	jmp GetNode
FileNotFound:
	jmp stop
	
GetNode:
	###b 0x
	movw LBA_I_Node_Start, %bx		
	movw %bx, dap_first_sector		#read i_node first block
	
	#read i-node from disk to dap
	movw $dap, %si
	movb diskid, %dl
	movb $0x42, %ah
	int  $0x13

	###b 0x7cd5
	#cl have i-node index
	movw $START_BUFFER, %bx
	
WhileWrongINode:
	decb %cl
	cmp  $0, %cl
	jle  GetFileDetails				#i-node found
	add  $I_NODE_SIZE,%bx			#index points to next i-node
	jmp	 WhileWrongINode


GetFileDetails:
	#get file size and 
	movw 8(%bx),%cx     			#get i_size: file size
	movw %cx,filesize_left			#save filesize

									#this way we add 4 to bx for each 
									#block in the while cicle 

	#prepare dap for loading file to memory
	#we want to load file to segment 0x1000, offset 0
	movw $0x1000, dap_dest_segment
	movw $0x0, dap_dest_offset
	
	#bx points to izone[0]
	#addw $24,%bx					#bx points to start of izone[0]
	addw $20,%bx					#subtracted 4 to add 4 begin while
	#start loading file to memory/reading zones
	movw $0, %cx					#counter for direct zone pointer
	###b 0x7cfa
	
LoadNextBlock:
	###b 0x7cfd
	addw $4, %bx					#next address/block	
	movw (%bx), %ax					#relative pointer to first block of file
	shlw $1, %ax					#ax * 1024 (size of block) / 512 (size of sector)
	addw LBA_Start, %ax	 			#add start of data to get absolute position
	
	movw %ax, dap_first_sector
	
	#LOAD FILE BLOCK FROM DISK
	
	movw $dap, %si
	movb diskid, %dl
	movb $0x42, %ah
	int  $0x13

	###b 0x7d16
	#need to check file size to see if we already finished
	#and if not prepare to read next block
	
	#check if we already finished file
	movw filesize_left, %dx
	subw $0x400, %dx
	cmpw $0, %dx
	jle  EndLoading
	movw %dx, filesize_left

	#update number of blocks read and pointer to next block to be read
	cmpb $I_NODE_MAX_DIRECT, %cl 
	### b 0x7d2f
	jg ContinueLoading
	je PrepareIndirect
	incb %cl
	jmp ContinueLoading
	
PrepareIndirect:
	incb %cl
	movw (%bx), %bx
	addw %bx, LBA_Data_Start
	movw %bx, dap_first_sector
	
	#save current dest segment and offset
	movw dap_dest_segment, %dx
	push %dx
	movw dap_dest_offset, %dx
	push %dx
	
	movw $0, dap_dest_segment
	movw $START_BUFFER, dap_dest_offset
	
	#read indirection block to memory
	movw $dap, %si
	movb diskid, %dl
	movb $0x42, %ah
	int  $0x13
	
	#retrieve dest segment and offset to continue reading file
	pop  dap_dest_offset
	pop  dap_dest_segment
	movw $START_BUFFER, %bx			#bx -> first pointer in indirection block
	subw $4, %bx					#get back 4 bytes because we will add 4 on next load
	
ContinueLoading:
	#now we need to correct de destination offset and
	#check if need to change segment - each segment has 64kb
	#if already loaded 63 blocks of 1kb then when adding another block 
	#cx will be 0 and we will have a carry
	movw dap_dest_offset, %dx
	addw $0x400, %dx
	jc ChangeSegment				#if carry then rolled over and need to change segment
	movw %dx, dap_dest_offset
	jmp LoadNextBlock

ChangeSegment:
	movw %dx, dap_dest_offset
	movw dap_dest_segment, %dx
	addw $0x1000, %dx
	movw %dx, dap_dest_segment
	jmp LoadNextBlock

EndLoading:
	ljmp $0x1000, $0				#long jump to area where where we loaded or file

stop:
	hlt
	jmp stop

.section .rodata         			# program constants (no real protection)
	filename: 				.asciz "lsc-boot.sys"
	filename_length:		.word 13
      
      
.data                    			
	filesize_left: 			.quad	0
	diskid: 				.word 	0	
	LBA_Start:				.quad	0
	LBA_I_Node_Start:		.quad	0
	LBA_Data_Start:			.quad	0
	
	dap:	
		dap_length:			.byte 	16  			# length of dap
		dap_dummy:			.byte 	0				# unused (default 0)
		dap_number_sectors:	.byte 	2				# number of sectors to read, up to 127 - 2 sectors = 1 block
		dap_dummy2:			.byte 	0				# unused (default 0)
		dap_dest_offset:	.word 	0x7E00			# destination offset of memory buffer
		dap_dest_segment:	.word 	0				# destination segment of memory buffer
		dap_first_sector:	.quad 	0				# LBA address of the first sector to read
		
						




