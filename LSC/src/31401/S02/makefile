# stage 2 - makefile for LSC.SYS

install: lsc-boot.sys
	# boot.sys created with sucess
	# remove old boot and replace for new
	/mnt/hgfs/Xubuntu/LSC/./mountpartitions
	rm -f /mnt/lsc/hd0p2/lsc.sys
	rm -f /mnt/lsc/hd0p2/lsc-boot.sys
	rm -f -r /mnt/lsc/hd0p2/.Trash-1000/
	# adding new .sys file
	cp lsc-boot.sys /mnt/lsc/hd0p2
	ln /mnt/lsc/hd0p2/lsc-boot.sys /mnt/lsc/hd0p2/lsc.sys
	# re-mounting partitions
	/mnt/hgfs/Xubuntu/LSC/./mountpartitions

lsc-boot.sys: start16.o start64.o prog.o counter.o minixUtils.o disk.o
	ld -T lsc64.ld -o $@ $^
	
start16.o: start16.s
	as --64 -o $@ $^

start64.o: start64.s
	as --64 -o $@ $^
	
prog.o: prog.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 
	
minixUtils.o: minixUtils.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 
	
disk.o: disk.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 

counter.o: counter.c
	gcc -m64 -ffreestanding -mno-red-zone -c -o $@ $^ 

clean:
	rm -f *.o *.fd *.sys
