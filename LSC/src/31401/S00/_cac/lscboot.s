 .equ START_ADDR, 0x7C00  	# .equ defines a textual substitution 
 .equ ROOT_DIR, 0x7e00
 .equ ROOT_DIR_DATA, 0x1000
 
 #INODE_ROOT_DIRECTORY -> 64+22202368=22202752/1024=2168.375*2=43364 ate 16 inodes o segment contina ser 43364
 .equ INODE_ROOT_DIRECTORY, 43364 
 .equ OFFSET_INODE_TO_GET_IZONE, 24
 .equ OFFSET_DAP_LBA_ADDRESS, 8
 .equ OFFSET_DAP_DESTINATION_OFFSET, 4
 .equ OFFSET_DAP_DESTINATION_SEGMENT, 6 #E9F4FA
 .equ LENGHT_FILE_TO_FIND, 8 # lsc.sys + /0 = 8 lenght
 .equ BASE_PARTITION, 43344
 .equ PROG_SEGM, 0x1000
 .equ STACK_TOP, 0x7C00
 
 .text                    	# code section starts here 
 .code16                 	# this is real mode (16 bit) code 
 cli                      	# no interrupts whille initializing 
 # ... init ...           	# initialization code... 
		ljmp $0, $norm_cs 
norm_cs: 
		xorw %ax, %ax 
		movw %ax, %ds 
		movw %ax, %ss
		movw %ax, %es
	
		movw $0x7C00, %sp
 sti                      	# interrupts enabled after initializing 
 
 # ... prog ...     
 # ..main program body...

#Ler do disco dir raiz	
	#drive id
		movb	%dl, drv
	#setup to use the bios service 
		movb	$0x42, 	%ah
		movb    drv  , 	%dl
		movw	$dap , 	%si
	#interrupt	
		int		$0x13
		#pointer to first file name of inode
		movl	$(ROOT_DIR + 2),%edi
		#save pointer on stack
		pushl	%edi
		#saving pointer of string to compare
		movl	$msgb, 	%esi
		
		movl	$LENGHT_FILE_TO_FIND,		   		%ecx	
strcmp:		
		repe	cmpsb
		test	%cx,	%cx
		jnz		reset#2398DF
		popl	%edi
		subl	$2,		%edi
#--------------------------------------inode find out---------------------------------------
		#edi = inode of file
		movw (%di), %ax
		#ax = struct number
		dec	%ax		
		#eax = offset of struct
		shll $6, %eax # 6 shifts = ax*64
		
		#ebx = address of inode
		movl %eax, %ebx
		
		#colocar posicao de memoria de dap em si
		movw $dap, %si
		
		#actualizar LBA adress
		movw $INODE_ROOT_DIRECTORY, OFFSET_DAP_LBA_ADDRESS(%si)
		
		
		#setup to use the bios service 
		movb	$0x42, 	%ah
		movb    drv  , 	%dl
		movw	$dap , 	%si
		#interrupt	
		
		int		$0x13
#--------------------------------------inode on memory---------------------------------------		
		#put pointer struct minix2_inode
		add $ROOT_DIR, %ebx	
		pushl %ebx
		#pointer to i_zone of struct will be save on global variable: i_zone
		add $OFFSET_INODE_TO_GET_IZONE, %ebx
		movw $i_zone, %si
		movw %bx, (%si)
		
		#get size of data, we should add offset to 0x7e00, in this case is 8
		popl %ebx
		add $8,%ebx
		movl (%ebx), %ebx
		#save size of file on global variable: size_file
		movw $size_file, %si
		movw %bx, (%si)
		
		#variable address_data has address disc to write data of file
		movw $address_data, %si
		movw $0, (%si)
		
		#put right address to write data of file
		movw $destination_offset, %si
		movw $0, %dx
		movw %dx, (%si) #0
		movw $destination_segment, %si
		movw $4096, %dx
		movw %dx, (%si) #1000 
		
		jmp readblocks
		
update_izone:
		#updating next i_zone to read and calculate next address on disc
		movw $i_zone, %si
		add $4, (%si)
		
update_size_address:		

		#sub 1024 on size to know exactly how many byte left
		#update_izone size, to inform that we already read 1024 bytes
		#movw $size_file, %si
		#movw (%si),%ax
		#sub $1024, %ax
		#movw %ax, (%si)
						
		#update_izone address to data
		movw $address_data, %bx
		movl (%ebx), %ebx
		add $0x400, %ebx
		pushl %ebx
		jnc update_dap_offset 	#if there is carry it needs that we will write on another sector
								#if we dont update_izone segment: 1000 to 2000, 2000 to 3000 that means
								#that we will override waht we wrote.
		xor	%ebx, %ebx
		movw $destination_segment, %bx
		movl (%ebx), %ebx
		addw $0x1000, %bx
		movw $destination_segment, %si
		movw %bx,(%si)
		
update_dap_offset:
		popl %ebx
		movw $address_data, %si
		movw %bx, (%si) #word = 16, long = 32
		mov $dap, %si
		movw %bx, OFFSET_DAP_DESTINATION_OFFSET(%si)
		
readblocks:
		# si = &dap (address dap)
		movw $i_zone, %bx
		
		#get pointer to i_zone
		movw (%bx), %bx
		#value of i_zone[index]
		movl (%bx), %eax
		
		#get absolute value of offset - start of partitin
		shll $10, %eax #eax*1024
		
		#divid by 512 to get value in sectors
		shrl $9, %eax #eax/512
		
		addl $BASE_PARTITION, %eax
		movw $first_sector, %si
		movl %eax, (%si)
		
		movb	$0x42, 	%ah
		movb    drv  , 	%dl
		movw	$dap , 	%si
		
		int 	$0x13
		movw 	$size_file, %si	
		movw	(%si), %bx
		sub 	$1024, %bx
		movw 	%bx, (%si)
		jng		stop
		movw 	$number_cicles, %si
		movw	(%si),%bx
		sub 	$1, %bx
		movw 	%bx, (%si)
		jz 		last_i_node
		jmp		update_izone
		
		
last_i_node:
		movw $i_zone, %si
		add $4, (%si)
		xor %eax,%eax
		movw $i_zone, %ax
		
		#get pointer to i_zone
		movl (%eax), %eax
		movl (%eax), %eax
		
		#get absolute value of offset - start of partitin
		shll $10, %eax #eax*1024
		
		#divid by 512 to get value in sectors
		shrl $9, %eax #eax/512
		
		addl $BASE_PARTITION, %eax
		movw $first_sector, %si
		movl %eax, (%si)
		
		movw	$destination_offset, %si
		pushl 	(%si)
		movw 	$ROOT_DIR, (%si)
		
		mov 	$destination_segment, %si
		pushl 	(%si)
		movw 	$0, (%si)
		
		movb	$0x42, 	%ah
		movb    drv  , 	%dl
		movw	$dap , 	%si
		
		int 	$0x13
				
		mov 	$i_zone, %si
		movl 	$ROOT_DIR, (%si)
		
		popl	%eax
		movw	$destination_segment, %si
		movw 	%ax, (%si)
		
		popl	%eax
		movw	$destination_offset, %si
		movw 	%ax, (%si)
		xor 	%eax,	%eax
		jmp 	update_size_address
		
# ... term ...           
# end of execution... 
		
stop: 
	  # Let dl carry boot drive id
      movb   STACK_TOP-2, %dl
	  ljmp   $PROG_SEGM, $0
		#hlt 
		#jmp stop 
 
reset:
		#restore pointer to third bit of inode which pointer reach file name that we compare before
		popl	%edi
		#add 32 bits to edi to reach next file name
		addl	$32,	%edi
		pushl	%edi
		# constant '12' number of comparisons -> length of string "lsc.sys"+/0
		movl	$LENGHT_FILE_TO_FIND,		%ecx
		#put esi on first character of "lsc.sys"
		movl	$msgb,	%esi
		jmp		strcmp
 
		.section .rodata         # program constants (no real protection) 
msgb: 	.asciz "lsc.sys" 	 #message.txt
msge: 
 
		.data                    # program variables (probably not needed) 
dap:
		#lenght of DAP
lenght_dap:
		.byte	16
		#unused
unused_dap:
		.byte	0
		#number of sectors
number_sector:
		.byte	2
		#unused
unused_dap_2:
		.byte	0
		#destination offset of memory buffer
destination_offset:
		.word	ROOT_DIR
		#destination segment of memory buffer
destination_segment:
		.word	0
		#LBA address of the first sector to read
first_sector:
		.quad	45150 # 0x160bc00 = 22192128, ((903*1024)+22192128)/512 = 45150	

#pointer to current i_zone, will be used to calculate sector of partition
i_zone:
		.word 0
#number of cicles until we reach point to point that it's a pointer to data
number_cicles:
		.word 7
#size of file
size_file:
		.word 0
#disc address that will be use to write data of file
address_data:
		.word 0
drv:
		.word	0
		
lsc:  	.long 2010 
 
		.end
		
