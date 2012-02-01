.equ START_ADDR, 0x7C00       # .equ defines a textual substitution
.equ MEM_BUFFER, 0x1000		  # mem buffer position
.equ FIRST_SECTOR 0xA950	  #first sector of LBA



.text                         # code section starts here
.code16                       # this is real mode (16 bit) code
cli                           # no interrupts while initializing
# ... init ...                # initialization code...
    ljmp $0, $norm_cs
norm_cs:
    xorw %ax        , %ax
    movw %ax        , %ds
    movw %ax        , %ss
    movw %ax        , %es
    movw START_ADDR, %sp 
    movb %dl        , driveid

sti                           # interrupts enabled after initializing

# ... prog ...                # main program body.../
main_prog:

	#read from disk
    movw  $dap  , %si		#read data to dap
    movb  driveid , %dl		#drive ID
    movb  $0x42	, %ah		#identify service to be called
    int   $0x13				#invoke BIOS service

	#read first data zone
	movw $dap, %si	
	movw 4(%si), %si 
	movw 24(%si), %si	
	sal $1, %si
	
	movw FIRST_SECTOR, %di	
	addw %di, %si				#add izone0 (first data zone) to base_address
	movw %si, dap+8		
	
	#read from disk - root directory
    movw  $dap  , %si		#read data to dap
    movb  driveid , %dl		#drive ID
    movb  $0x42	, %ah		#identify service to be called
    int   $0x13				#invoke BIOS service


	#find files present on disk
	movw $0x7E42, %si
	movw $filename, %di
	movw filename_length, %cx
	
file_search:
	cmp $0, -2(%si)
	jz file_not_found
	
	cld
	repe cmpsb		
	cmp $0, %cx		
	jz file_found
	addw $0x20, %si
	jmp file_search
	
	#here cx has the inode number for the file we want
	
file_found:
	subw filename_length, %si
	movb -2(%si), %cl		
	jmp get_nodes
file_not_found:
	jmp stop

get_nodes:
	movw $0xA964, dap+8	## need to get to inodes start

	# now get the inodes zone
	#read from disk
    movw  $dap  , %si		#read data to dap
    movb  driveid , %dl		#drive ID
    movb  $0x42	, %ah		#identify service to be called
    int   $0x13				#invoke BIOS service

	
	movw $0x7E00, %bx	#bx points to beggin of inodes area

	#cx have index nmber for inode
get_next_node:
	cmp $1,%cl
	je read_node
	addw $64, %bx		#each inode has 64 bytes
	decb %cl
	jmp get_next_node

read_node:
	movw 8(%bx),%cx 	#filesize
	movw %cx, filesize	#save filesize for further use
	
	addw $20,%bx
	movb $1, %cl

	movw $0x0, dap+4
	movw $0x1000, dap+6	 

read_zone: 
	addw $4,%bx		# bx has the data zone address of the file we want
	

read_zone_indirection: 	# after loading first indirection addr to %bx we dont increment it, bcause the first addr is already in the register

	movw (%bx),%ax 			#now AX contains the data zone addr
	sal $1,%ax
	addw $FIRST_SECTOR,%ax 	#ax now has the LBA Address -> save on DAP
	movw %ax, dap+8
	
	#read data - file: lsc.sys
	#read from disk
    movw  $dap  , %si		#read data to dap
    movb  driveid , %dl		#drive ID
    movb  $0x42	, %ah		#identify service to be called
    int   $0x13				#invoke BIOS service

	
	#here we need to check next memory address because of segment change!
	movw dap+4, %dx	
	addw $0x400, %dx 
	jnc continue_read
	
	#if has carry we need to increment memory segment
	movw dap+6,%dx
	addw $0x1000,%dx
	movw %dx, dap+6
	xor %dx, %dx	#start segment, dx=0

continue_read:
	movw %dx, dap+4
	
	#check if file was all read. if read > size exits
	xorw %dx, %dx
	movb %cl, %dl
	sal $10,%dx
	cmp filesize, %dx
	jg end

	#increment read count. if read count=8 need to get pointer to new zone
	incb %cl
	cmpb $8,%cl
	jne read_zone 
	
	#get pointer to indirection to next zone
	#after get new address, save it DAP, and load values from disk
	addw $4,%bx
	
	#push bx to save the address of first zone to have it in the end
	push %bx
	movw (%bx), %ax
	sal $1,%ax
	addw $first_sector,%ax
	
	movw %ax, dap+8
	
	#save dap memory segment and offset, read new data from disk and return
	#to "old" addresses
	push dap+4
	push dap+6
	movw $8200,%bx
	movw %bx, dap+4
	movw $0,dap+6
	
	#read from disk
    movw  $dap  , %si		#read data to dap
    movb  driveid , %dl		#drive ID
    movb  $0x42	, %ah		#identify service to be called
    int   $0x13				#invoke BIOS service

	pop dap+6
	pop dap+4
	#read new zone as usual
	jmp read_zone_indirection

end:
	pop %bx
	ljmp $0x1000, $0

stop:
    hlt
    jmp stop


.section 
.rodata         			# program constants (no real protection)
	#first sector to be read
	first_sector: 		.word 0xA950	
	filename: 			.asciz "lsc.sys"
	filename_length:	.word 8
	

.data 	                        # program variables (probably not needed)




driveid: 
	.word  0
dap: 
	 .byte 	16        	# length  
     .byte 	0         	# unused (default 0)
     .byte 	2         	# number of sectors to read, up to 127
	 .byte 	0         	# unused (default 0)
	 .short 0x7D00   	# destination offset of memmory buffer
	 .short 0        	# destination segment of memmory buffer
	 .quad 	0xA964    	# LBA address of first sector to read
.end
