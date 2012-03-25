.equ START_ADDR,	0x7C00 			# .equ defines a textual substitution
.equ START_BUFFER,	0x7E00			
.equ MEM_BUFFER, 	0x1000		  	# mem buffer position

.equ DISK_ROOT_ADDR,				0x0
.equ PARTITION_TABLE_START_ADDR, 	446
.equ PARTITION_ACTIVE,				0x80
.equ PARTITION_INFO_SIZE,			16

.equ BLOCK_LBA_SIZE,				2
.equ SECTORS_PER_BLOCK,				2



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
	###b 0x7c50
	
	#get I-Node zone start LBA address
	addw $BLOCK_LBA_SIZE, %bx		#plus 2 sectors for SuperBlock
	#add  s_imap_blocks * 2
	movw $START_BUFFER, %si
	addw $4, %si
	movw (%si), %cx
	imulw $2, %cx
	addw %cx, %bx
	#add  s_zmap_blocks
	addw $2, %si
	movw (%si), %cx
	imulw $2, %cx
	addw %cx, %bx
	movw %bx, LBA_I_Node_Start
	
	#get I-Node root to find where is lsc-boot.s
	movw %bx, dap_first_sector
	
	#read I-Node root from disk to dap
	movw $dap, %si
	movb diskid, %dl
	movb $0x42, %ah
	int  $0x13
	
	###b 0x7c7d
	

	

.section .rodata         			# program constants (no real protection)
	filename: 			.asciz "lsc-boot.sys"
	filename_length:	.word 13
	first_sector: 		.word 0xA950		#first sector...
      
      
.data                    			
	filesize: 				.int	0
	diskid: 				.word 	0	
	LBA_Start:				.quad	0
	LBA_I_Node_Start:		.quad	0

	
	dap:	
		dap_length:			.byte 	16  			# length of dap
		dap_dummy:			.byte 	0				# unused (default 0)
		dap_number_sectors:	.byte 	2				# number of sectors to read, up to 127 - 2 sectors = 1 block
		dap_dummy2:			.byte 	0				# unused (default 0)
		dap_dest_offset:	.word 	0x7E00			# destination offset of memory buffer
		dap_dest_segment:	.word 	0				# destination segment of memory buffer
		dap_first_sector:	.quad 	0				# LBA address of the first sector to read
		#903 zonas = 1806 sectors + LBA Start Offset 43344 = 45150 - first sector to be read
						




