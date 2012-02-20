DSK_HOME = /home/masterzdran/WorkingArea/Isel/semestre-7/LSC/dsk

install: lsc-boot.sys lscboot.bin
	rm -f /mnt/lsc/hd0p2/*.sys
	cp lsc-boot.sys /mnt/lsc/hd0p2/
	ln /mnt/lsc/hd0p2/lsc-boot.sys /mnt/lsc/hd0p2/lsc.sys

lscboot.bin: lscboot.o
	ld -T bootrec.ld -o lscboot.bin lscboot.o
	dd if=lscboot.bin of=$(DSK_HOME)/lsc-hd63-flat.img bs=512 seek=43344 count=1 conv=notrunc
	
lscboot.o: lscboot.s
	as -o lscboot.o lscboot.s
	
start16.o: start16.s
	as --64 -o $@ $^

start64.o: start64.s
	as --64 -o $@ $^
	
Main.o: Main.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 

timer.o: timer.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 

ata.o: ata.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 

BMP.o: BMP.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 

minix_fs.o: minix_fs.c
	gcc -m64 -fno-stack-protector -ffreestanding -mno-red-zone -c -o $@ $^ 

VESA.o: VESA.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 

lsc-boot.sys: start16.o start64.o timer.o ata.o minix_fs.o BMP.o  VESA.o  Main.o 
	ld -T lsc64.ld -o $@ $^

clean:
	rm -f *.o *.fd *.sys *.bin
	
