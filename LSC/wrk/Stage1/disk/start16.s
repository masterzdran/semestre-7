
      # Adapted from:
      # http://wiki.osdev.org/Entering_Long_Mode_Directly

      .equ   START16_SEG, 0x1000
      .equ   GET_VIDEO_MODE, 0x4f00
      .equ   GET_VIDEO_MODE_DETAIL, 0x4f01
			.equ	 VIDEO_MODES, 0x6	


      .section .rodata
VESA_MAGIC:	
      .ascii "VBE2"
                            
      .text
      .code16
      
start16:
      movw   $START16_SEG, %ax
      movw   %ax, %ds

      # GET VESA INFO
      movl  VESA_MAGIC, %eax			#move VBE2 to begin of signature
      movl  %eax, signature     
      movl  $vesa_info, %edi			#es:di points to vesa_info
      movw  $START16_SEG, %ax
      movw  %ax, %es     
      movw  $GET_VIDEO_MODE, %ax
      int   $0x10

			#####at this point es:di have list of available video modes

      # GET VESA INFO DETAIL

get_vesa_info_detail:
      movw  $6, %cx 
      movl  $vesa_mode_info, %edi
      movw  %ds, %ax
      movw  %ax, %es 
      
      movw  $GET_VIDEO_MODE_DETAIL, %ax
      int   $0x10



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

      movw   $0x018f, %ax
      stosw

      xorw   %ax, %ax
      movw   $0x07ff, %cx
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



      .data

vesa_info_array:
		.space		VIDEO_MODES*256


vesa_info:
		signature:			   	.long
		version:			      .word
		vendor_str_off:			.word
		vendor_str_seg:			.word
		capabilities:		   	.long
		video_mode_ptr:   	.long    #offset + seg
		total_memory:		   	.word
                        .space	748

vesa_mode_info:
      mode_attr:           .word 0
      win_attr:            .byte 0
                           .byte 0
      win_grain:           .word 0
      win_size:            .word 0
      win_seg:             .word 0
                           .word 0
      win_scheme:          .long 0  #offset + seg
      logical_scan:        .word 0
      
      h_res:               .word 0
      v_res:               .word 0
      char_width:          .byte 0
      char_height:         .byte 0
      memory_planes:       .byte 0
      bpp:                 .byte 0
      banks:               .byte 0
      memory_layout:       .byte 0
      bank_size:           .byte 0
      image_planes:        .byte 0
      page_function:       .byte 0

      rmask:               .byte 0
      rpos:                .byte 0
      gmask:               .byte 0
      gpos:                .byte 0
      bmask:               .byte 0
      bpos:                .byte 0
      resv_mask:           .byte 0
      resv_pos:            .byte 0
      dcm_info:            .byte 0

      lfb_ptr:             .long 0     /*Linear frame buffer address */
      offscreen_ptr:       .long 0     /*Offscreen memory address */
      offscreen_size:      .word 0
      
      reserved:            .space 206 

.end
