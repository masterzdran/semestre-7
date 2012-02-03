#include "ata.h"
// Source :  http://wiki.osdev.org/ATA_PIO_Mode

static U32 ATA_disk_is_ready(U16 bus) //0x1F7
{
	inb(bus);
	inb(bus);
	inb(bus);
	inb(bus);
	
	while( ( inb(bus) & 0x80 ) == 0x80  ||  ( inb(bus) & 0x29 ) == 0 ); // check if drive is busy or if all of those are clear
	if((inb(bus)&0x21)!=0) return -1; //return error...
	return 0; 
}

U32 ATA_read(U16 * destination_buffer, U32 LBA, U16 nr_sectors){
	U32 base;
	U32 count;
	U32 count_sectors;
	U32 slave=0;
	
	//int LBA = 0xA950; // absolute LBA addr representing the beggining of the X partition
	
	//stop the current device from sending interrupts on primary bus...
	outb(1,0x3F6);
				
	//Send a NULL byte to port 0x1F1, if you like (it is ignored and wastes lots of CPU time): 
	outb(0x00,0x1F1);
	
	//Send the sectorcount to port 0x1F2: 
	outb((U8) nr_sectors, 0x1F2);
	
	//Send the low 8 bits of the LBA to port 0x1F3: 
	outb((U8) LBA, 0x1F3);
	
	//Send the next 8 bits of the LBA to port 0x1F4: 
	outb((U8)(LBA >> 8), 0x1F4);
	
	//Send the next 8 bits of the LBA to port 0x1F5: 
	outb((U8)(LBA >> 16), 0x1F5);
	
	//Send 0xE0 for the "master" or 0xF0 for the "slave", ORed with the highest 4 bits of the LBA to port 0x1F6:
	outb(0xE0 | (slave << 4) | ((LBA >> 24) & 0x0F), 0x1F6);
	
	//Send the "READ SECTORS" command (0x20) to port 0x1F7: 
	outb(0x20, 0x1F7);

	//Transfer 256 words, a word at a time, into your buffer from I/O port 0x1F0.
	//Then loop back to waiting for the next IRQ (or poll again -- see next note) for _EACH SUCCESSIVE SECTOR_.
	for( count_sectors = 0 ; count_sectors < nr_sectors ; ++count_sectors )
	{
		base = count_sectors*256;
		
		//Wait for an IRQ or poll.
		if(ATA_disk_is_ready(0x1F7)<0) return -1;
		
		for( count = 0 ; count<256 ; ++count )
		{
			destination_buffer[count + base] = inw(0x1F0);
		}
	}
	
	return 0;	
}
