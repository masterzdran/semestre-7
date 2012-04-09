
      # Adapted from:
      # http://wiki.osdev.org/Entering_Long_Mode_Directly

	.equ	START16_SEG, 0x1000
	
	.equ	GET_VIDEO_MODE, 0x4f00
	.equ	GET_VIDEO_MODE_DETAIL, 0x4f01
	.equ	SET_VIDEO_MODE, 0x4f02
    
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
      
#Get graphic card, choose mode and activate it
	
	#GET VESA INFO
	### b 0x10021
	
    movw  $vesa_info, %di			#es:di points to vesa_info
    movw  %ds, %ax
    movw  %ax, %es
    movw  $GET_VIDEO_MODE, %ax
    int   $0x10
    
    #es:di have general video info and pointer to 
    #list of available video modes on video_mode_ptr offset and segment
    
    #will save list of available video modes in fs:di and cicle
    
    ###b 0x1002d
    
    movw %es:14(%di), %si	#video_mode_ptr_off
	movw %es:16(%di), %ax	#video_mode_ptr_seg
	movw %ax, %fs
	
	movw $vesa_mode_detail, %di	#will have details here
    
		#cicle through video modes to find the one we want
		#video modes start in fs:di and end with 0xFFFF
    
		#To get information about a specific video mode, use INT 0x10 
		#with AX set to 0x4F01, CX set to the specific video mode id, 
		#and ES:DI pointing to a 256 byte buffer
	
	subw $2, %si		#to compensate go to video mode on the first time
	
cicle_video_mode:

	addw $2, %si 		#go to next video mode
	
    movw %fs:(%si), %cx 	#save ID of video mode on cx
    cmpw $0xFFFF, %cx		#check if end of list
    je   video_mode_end
    
		#get detail of video mode
    movw $GET_VIDEO_MODE_DETAIL, %ax
    int  $0x10
	
	###b 0x1004d
		#After calling the service, AL should have the value 0x4F and 
		#AH will hold the error code, with 0x00 meaning  success
	cmpw $0x004F, %ax
	jne  video_mode_end
	
	#video mode detail on es:di
	#check for resolution and bpp
	
	#check h_res
	
	movw %es:18(%di), %bx
	cmpw $800, %bx
	jne cicle_video_mode
	#check v_res
	movw %es:20(%di), %bx
	cmpw $600, %bx
	jne cicle_video_mode
	#check bpp
	movb %es:25(%di), %bl
	cmpb $16, %bl
	jne cicle_video_mode
	
	#here we have the video mode we want
	#Set the system into graphics mode, by using BIOS service INT 0x10, 
	#with AX=0x4f02 and BX set to the video mode id 800x600 at 16bpp
	#and bit 14 set to 1,to request access via the linear frame buffer. 
	
	#cx could have been replaced on interrupt
	movw %fs:(%si), %cx
	#request access via LFB
	orw  $(1<<14), %cx
	movw $SET_VIDEO_MODE, %ax
	int  $0x10
	
	###b 0x1007b
	
#if we cant find the video mode we want we continue with the default one	
video_mode_end:
	
      # Build page tables

      xorw   %bx, %bx
      movw   %bx, %es
      cld	#clear direction flags for stosw
      movw   $0xa000, %di
#es:di  pointing to begining of first page of indexes

#Clear PML4 Table. First entry (A000) points to PDP(B00F)
      movw   $0xb00f, %ax
      stosw

      xorw   %ax, %ax
      movw   $0x07ff, %cx	#2047 remaining for block of 4096 bytes
      rep    stosw

#Clear PDP Table. First entry (B000) points to PDP(C00F)
      movw   $0xc00f, %ax
      stosw

      xorw   %ax, %ax
      movw   $0x07ff, %cx	#2047 remaining for block of 4096 bytes
      rep    stosw

#PDE table - last table before physical address
#here we need to add entries pointing to the LFB of the video card
#PDE entries must end with $0x018f for PDE flags (page 4-36)
#bits from 21 to 29 of linear address

#we need to create an entry for LFB but need to have some extra pages first
	###b 0x10099

	#first blank page
	movl  $0x0000018f, %eax
	stosl
	xorl %eax, %eax
	stosl
	#second blank page
	movl  $0x0020018f, %eax	#add 1 bit to bit 22 (first bit not offset)
	stosl
	xorl %eax, %eax
	stosl
	
	movl vesa_mode_detail+40, %eax	#position of lfb pointer
	andl $0xffe00000, %eax	#clear bitos 0-20 (offset) of address
	orl  $0x0000018f, %eax	#'add' value of PDE flags
	stosl
	xorl %eax, %eax
	stosl

	movl vesa_mode_detail+40, %eax	#position of lfb pointer
	andl $0xffe00000, %eax	#clear bitos 0-20 (offset) of address
	addl $0x00200000, %eax  #next page - bit 21
	orl  $0x0000018f, %eax	#'add' value of PDE flags
	stosl
	xorl %eax, %eax
	stosl
	
      xorw   %ax, %ax
      #movw   $0x07ff, %cx
      movw   $0x07f0, %cx	
      rep    stosw

      # Enter long mode

      cli                       # No IDT. Keep interrupts disabled.

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

vesa_info:
	signature:			.ascii	"VBE2"	#long
	version:			.word	0
	vendor_str:			.long	0 #offset + seg
	capabilities:		.long	0
	video_mode_ptr_off:	.word	0 #offset
	video_mode_ptr_seg:	.word 	0 #segment
	total_memory:		.word	0
						.space	748

.global vesa_mode_detail

vesa_mode_detail:
	.space 256
	
	


      .end
