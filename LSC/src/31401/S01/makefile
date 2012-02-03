# stage 1 - makefile for LSC.SYS
install: lsc-boot.sys
	rm -f /mnt/lsc/hd0p2/*.sys
	cp lsc-boot.sys /mnt/lsc/hd0p2
	ln /mnt/lsc/hd0p2/lsc-boot.sys /mnt/lsc/hd0p2/lsc.sys

lsc-boot.sys: start16.o start64.o prog.o timer.o
	ld -T lsc64.ld -o $@ $^
	
start16.o: start16.s
	as --64 -o $@ $^

start64.o: start64.s
	as --64 -o $@ $^
	
prog.o: prog.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 

timer.o: timer.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 

clean:
	rm -f *.o *.fd *.sys
