
		# Adapted from:
		# http://wiki.osdev.org/Entering_Long_Mode_Directly

		.equ   START16_SEG, 0x1000

		.text
		.code16
start16:
		movw   $START16_SEG, %ax
		movw   %ax, %ds

		# Save boot drive id

		movl   $bootdrv, %eax   # eax <- Linear address with 20 bits
		movw   %ax, %di
		andw   $0xf, %di        # di <- lowest 4 bits of eax
		shrl   $4, %eax
		movw   %ax, %es         # es <- remaining 16 bits from eax
		movb   %dl, %es:(%di)   # save dl at es:di

		# Enable A20

		inb    $0x92, %al
		orb    $0x02, %al
		andb   $0xfe, %al
		outb   %al, $0x92


##### Graphics Info #####

		movw	%ds, %ax
		movw	%ax, %es
		movw  	$vesa_info, %di
		movw	$0x4F00, %ax
		int 	$0x10

		movw 	vesa_info+14, %si
		movw 	vesa_info+16, %ax
		movw 	%ax,%fs
		movw  	$vesa_mode_info, %di
	  
mode_info_store:
		cmpw 	$0xFFFF, %fs:(%si)
		je	done

		movw 	%fs:(%si), %cx	  
		movw	$0x4F01, %ax
		int 	$0x10

		cmpb	$16, 25(%di)
		je	res
		jmp	no_match
	  
res:
		cmpw	$800, 18(%di)
		jne	no_match
		cmpw	$600, 20(%di)
		je	match

no_match:
		addw  $2, %si			#next video mode
		jmp mode_info_store
	
match:
		## chamar interrupção- select video mode
		movw  %fs:(%si), %bx
		orw 	$(1<<14), %bx
		movw	$0x4F02, %ax
		int 	$0x10

done:
	  
		# Build page tables
		xorw   %bx, %bx
		movw   %bx, %es
		cld
		movw   $0xa000, %di

		movw   $0xb00f, %ax
		stosw 

		xorw   %ax, %ax
		movw   $0x07ff, %cx
		rep    stosw

		movw   $0xc00f, %ax
		stosw

		xorw   %ax, %ax
		movw   $0x07ff, %cx
		rep    stosw

		## [1st to 4th] 2MB page
		#movl $0, %ecx
		
page_directory_cicle:
		cmpw $4,%cx
		je end_page_directory_cicle
		shll $21, %ecx 

		movl %ecx,%eax 
		orl $0x018f, %eax ## set correct flags to save in PD table
		stosl
		xorl %eax, %eax
		stosl

		shrl $21, %ecx
		incw %cx
		jmp page_directory_cicle
      
 end_page_directory_cicle:
 
		## LFB addr @ 5th entry of the PD
		movl vesa_mode_info+40, %eax ## pointer to grafic card
		shrl $21, %eax
		//andl $0xFFF, %eax ## clean anything that we dont want
		shll $21, %eax
		orl $0x018f, %eax ## set correct flags to save in PD table

		stosl ## affect entry in the table
		xorl %eax, %eax
		stosl

		## LFB addr + 2MB @ 6th entry of the PD
		movl vesa_mode_info+40, %eax ## pointer to grafic card
		shrl $21, %eax
		incl %eax
		shll $21, %eax
		orl $0x018f, %eax ## set correct flags to save in PD table
		stosl ## affect entry in the table
		xorl %eax, %eax
		stosl

		## zero out the rest of the PD table
		xorw   %ax, %ax
		movw   $0x07ea, %cx ## (2048 - 24 ) entries -> 24 = 4 bytes * 6 entries
		rep    stosw

		# Enter long mode

		cli                       # No IDT. Keep interrupts disabled.

		/*
		If CR0.PG = 0, paging is not used the logical processor treats all linear addresses as
		if they were physical addresses.
		*/

		## CR4.PGE enables global pages.

		movl   $0xA0, %eax        # Set PAE and PGE
		movl   %eax, %cr4

		movl   $0x0000a000, %edx  # Point CR3 at PML4
		movl   %edx, %cr3

		movl   $0xC0000080, %ecx  # Specify EFER MSR

		rdmsr
		orl    $0x00000100, %eax  # Enable long mode
		wrmsr

		movl   %cr0, %ebx
		orl    $0x80000001, %ebx  # Activate long mode
		movl   %ebx, %cr0         # by enabling paging and protection simultaneously

		lgdtl  gdt_ptr            # Set Global Descriptor Table

		ljmpl  $1<<3, $start64    # Jump to 64-bit code start

		.align 16
gdt:
		.quad  0x0000000000000000
		.quad  0x0020980000000000
		.quad  0x0000900000000000

gdt_ptr:
		.word  (gdt_ptr-gdt-1)
		.long  (START16_SEG*16+gdt)
		.long  0

		.data      
vesa_info:
		.ascii 	"VBE2"			# u32 signature;          /* 0 Magic number = "VBE2" */
		.word	0				# u16 version;            /* 4 */
		.long	0				# far_ptr vendor_string;  /* 6 */
		.long	0				# u32 capabilities;       /* 10 */
		.long	0				# far_ptr video_mode_ptr; /* 14 */
		.space  788
		
		.global vesa_mode_info
	  
vesa_mode_info:
		.space  256

      .end
