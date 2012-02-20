DSK_HOME = /mnt/hgfs/D/ISEL/Semestre7/LSC/dsk

vbr: lscboot.bin
	dd if=lscboot.bin of=$(DSK_HOME)/lsc-hd63-flat.img bs=512 seek=43344 count=1 conv=notrunc

lscboot.bin: lscboot.o
	ld -T bootrec.ld -o lscboot.bin lscboot.o
	
lscboot.o: lscboot.s
	as -o lscboot.o lscboot.s
	
clean:
	-rm -f *.bin *.o *.fd
