.equ START_ADDR, 0x7C00       # .equ defines a textual substitution
.equ MEM_BUFFER, 0x1000		  # mem buffer position
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
    movw $START_ADDR, %sp 
    movb %dl        , driveid

sti                           # interrupts enabled after initializing

# ... prog ...                # main program body.../
main_prog:
    movw  $dap       , %si
    movb  $0x42      , %ah
    movb  driveid    , %dl
    int   $0x13
    jc    PrintError
    pushw %si
    movw  $msgb      , %si
    call  PrintInfo
    popw  %si
    jmp   stop
    
PrintError:
    pushw %si
    movw  $msgerror, %si
    call  PrintInfo
    popw  %si
    jmp   stop
    

PrintInfo:
	pushw %bp
    movw  %sp, %bp
    lodsb                        # load next byte from string from SI to AL
    or    %al, %al               # Does AL=0?
    jz    PrintDone              # Yep, null terminator found-bail out
    movb  $0x0E, %ah             # Nope-Print the character
    int   $0x10
    jmp   PrintInfo              # Repeat until null terminator found
    
PrintDone:
    popw  %bp
    
    ret
    
    
    movb $'@'    , %al         # The character: â@â
    movb $7      , %bl         # Light gray on black
    xorb %bh     , %bh         # Using page 0
    movb $0x0E   , %ah         # Identifying the service
    int  $0x10                 # Invoking the BIOS service
    
# ... term ...                 # end of execution...

init_vga:
    mov %es, 0xB800

stop:
    hlt
    jmp stop

.section .rodata                  # program constants (no real protection)
msgb: 		.ascii "Starting LSC..."
			.byte  13, 10, 0
			
msgerror: 	.ascii "Error while loading LSC..."
			.byte  13, 10, 0


.data 	                        # program variables (probably not needed)
driveid:
    .byte  0
lsc:
    .long 2011
dap:
    .byte  16
    .byte  0
    .byte  2
    .byte  0
    .word  0
    .word  MEM_BUFFER
    .quad 45150
.end
