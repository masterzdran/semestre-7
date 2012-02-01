
      # Adapted from:
      # http://wiki.osdev.org/Entering_Long_Mode_Directly

      .equ   START16_SEG , 0x1000      
      
      .global vesa_general_info
      .global vesa_mode_info

      .text
      .code16
start16:
      movw   $START16_SEG, %ax
      movw   %ax, %ds

      ### GET SUPPORTED VESA MODES
      mov $0x4f00, %ax          # BIOS SERVICE
      mov $START16_SEG, %bx     # SET ES:DI FOR DESTINATION BUFFER
      mov %bx, %es
      mov $vesa_general_info, %di
      int $0x10                 # CALL IRQ

      ### PRINT VIDEO MODES INFO 
      # GET video_mode_ptr      
      mov %es:0x0e(%di), %si     # OFFSET
      mov %es:0x10(%di), %fs     # SEGMENT
      
      # SET video_info_buffer ptr
      mov $START16_SEG, %bx
      mov %bx, %es
      mov $vesa_mode_info, %di
         
      # Check all modes
      loop:      
      mov %fs:(%si), %cx    # VIDEO MODE ID      
      mov $0xFFFF, %ax      # END OF LIST?
      cmp %cx, %ax
      je continue
      mov $0x4f01, %ax      # BIOS SERVICE
      int $0x10             # CALL IRQ
      #add $2, %si           # NEXT VIDEO MODE
      #jmp loop
      
      continue:
      
      # Save boot drive id
      
      movl   $bootdrv, %eax   # eax <- Linear address with 20 bits0Eh    DWORD   pointer to list of supported VESA and OEM video modes
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

vesa_general_info:
    .space 768

vesa_mode_info:
    .space 256

      .end
