all:
	as -o lscboot.o lscboot.s
	ld -T bootrec.ld -o lscboot.bin lscboot.o
	cp lsc.fd ../../dsk/ 
