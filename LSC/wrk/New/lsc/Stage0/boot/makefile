vbr: lscboot.bin
	dd if=lscboot.bin of=/home/isel/lsc/disks/lsc-hd63-flat.img bs=512 seek=43344 count=1 conv=notrunc

lscminixboot.bin: lscminixboot.o
	ld -T bootrec.ld -o lscminixboot.bin lscminixboot.o
	
lscminixboot.o: lscminixboot.s
	as -o lscminixboot.o lscminixboot.s
	
clean:
	-rm -f *.bin *.o *.fd
